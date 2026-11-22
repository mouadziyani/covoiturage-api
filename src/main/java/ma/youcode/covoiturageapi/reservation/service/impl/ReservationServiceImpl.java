package ma.youcode.covoiturageapi.reservation.service.impl;

import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.ForbiddenActionException;
import ma.youcode.covoiturageapi.exception.InvalidRequestException;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.passager.entity.Passager;
import ma.youcode.covoiturageapi.passager.repository.PassagerRepository;
import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;
import ma.youcode.covoiturageapi.reservation.entity.Reservation;
import ma.youcode.covoiturageapi.reservation.mapper.ReservationMapper;
import ma.youcode.covoiturageapi.reservation.repository.ReservationRepository;
import ma.youcode.covoiturageapi.reservation.service.inter.ReservationService;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import ma.youcode.covoiturageapi.stoppoint.repository.StopPointRepository;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final TripRepository tripRepository;
    private final StopPointRepository stopPointRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationRepository reservationRepository, PassagerRepository passagerRepository,
                                  TripRepository tripRepository, StopPointRepository stopPointRepository,
                                  ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.passagerRepository = passagerRepository;
        this.tripRepository = tripRepository;
        this.stopPointRepository = stopPointRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationResponseDto create(ReservationRequestDto request) {
        Passager passager = passagerRepository.findById(request.passagerId())
                .orElseThrow(() -> new NotFoundException("Passenger not found"));
        Trip trip = tripRepository.findByIdForUpdate(request.tripId())
                .orElseThrow(() -> new NotFoundException("Trip not found"));
        if (trip.getStatus() != StatutTrajet.PUBLIE) {
            logger.warn("Reservation rejected: trip {} is not published", trip.getId());
            throw new ConflictException("Only published trips can be reserved");
        }
        StopPoint boarding = findStopPoint(request.boardingStopPointId());
        StopPoint destination = findStopPoint(request.alightingStopPointId());
        checkStopPointBelongsToTrip(boarding, trip);
        checkStopPointBelongsToTrip(destination, trip);
        if (boarding.getStopOrder() >= destination.getStopOrder()) {
            logger.warn("Reservation rejected: boarding stop {} is not before destination stop {}",
                    boarding.getId(), destination.getId());
            throw new InvalidRequestException("Boarding stop must come before destination stop");
        }
        if (request.numberOfSeats() == null || request.numberOfSeats() < 1) {
            logger.warn("Reservation rejected: requested seat count is invalid for trip {}", trip.getId());
            throw new ConflictException("At least one seat must be reserved");
        }
        if (reservationRepository.existsByTripIdAndPassagerIdAndStatus(
                trip.getId(), passager.getId(), StatutReservation.CONFIRMEE)) {
            logger.warn("Reservation rejected: passenger {} already has a confirmed reservation for trip {}",
                    passager.getId(), trip.getId());
            throw new ConflictException("Passenger already has a confirmed reservation for this trip");
        }
        long reservedSeats = reservationRepository.sumSeatsByTripAndStatus(trip.getId(), StatutReservation.CONFIRMEE);
        int availableSeats = trip.getProposedSeats() - (int) reservedSeats;
        if (request.numberOfSeats() > availableSeats) {
            logger.warn("Reservation rejected: trip {} has {} seats available; {} requested",
                    trip.getId(), availableSeats, request.numberOfSeats());
            throw new ConflictException("There are not enough available seats");
        }

        BigDecimal unitPrice = destination.getPriceFromDeparture().subtract(boarding.getPriceFromDeparture());
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Reservation rejected: segment price is negative for trip {}", trip.getId());
            throw new ConflictException("Destination price must be greater than boarding price");
        }
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.numberOfSeats()))
                .setScale(2, RoundingMode.HALF_UP);

        Reservation reservation = reservationMapper.toEntity(request, passager, trip, boarding, destination, totalAmount);
        Reservation saved = reservationRepository.save(reservation);
        logger.info("Reservation {} created for trip {} by passenger {} ({} seat(s))",
                saved.getId(), trip.getId(), passager.getId(), request.numberOfSeats());

        if (availableSeats - request.numberOfSeats() == 0) {
            trip.setStatus(StatutTrajet.COMPLET);
            tripRepository.save(trip);
        }
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDto getById(Long reservationId, Long passagerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (!reservation.getPassager().getId().equals(passagerId)) {
            logger.warn("Reservation access rejected: passenger {} does not own reservation {}",
                    passagerId, reservationId);
            throw new ForbiddenActionException("This reservation belongs to another passenger");
        }
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getByPassagerId(Long passagerId) {
        if (!passagerRepository.existsById(passagerId)) {
            throw new NotFoundException("Passenger not found");
        }
        return reservationRepository.findByPassagerIdOrderByReservationDateDesc(passagerId)
                .stream().map(reservationMapper::toResponse).toList();
    }

}
