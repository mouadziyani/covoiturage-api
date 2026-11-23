package ma.youcode.covoiturageapi.trip.service.impl;

import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.car.repository.CarRepository;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.ForbiddenActionException;
import ma.youcode.covoiturageapi.exception.InvalidRequestException;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.reservation.repository.ReservationRepository;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import ma.youcode.covoiturageapi.stoppoint.mapper.StopPointMapper;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripSearchResultDto;
import ma.youcode.covoiturageapi.common.dto.PageResponse;
import ma.youcode.covoiturageapi.trip.mapper.TripMapper;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import ma.youcode.covoiturageapi.trip.service.inter.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional
public class TripServiceImp implements TripService {
    private static final Logger logger = LoggerFactory.getLogger(TripServiceImp.class);

    private final TripRepository tripRepository;
    private final CarRepository carRepository;
    private final TripMapper tripMapper;
    private final StopPointMapper stopPointMapper;
    private final ReservationRepository reservationRepository;

    public TripServiceImp(TripRepository tripRepository, CarRepository carRepository, TripMapper tripMapper,
                          StopPointMapper stopPointMapper, ReservationRepository reservationRepository) {
        this.tripRepository = tripRepository;
        this.carRepository = carRepository;
        this.tripMapper = tripMapper;
        this.stopPointMapper = stopPointMapper;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public TripResponseDto create(TripRequestDto request) {
        Car car = carRepository.findById(request.carId()).orElseThrow(() -> new NotFoundException("Car with id : " + request.carId() + " not found"));

        if (!car.getDriver().getId().equals(request.driverId())) {
            logger.warn("Trip creation rejected: driver {} does not own car {}", request.driverId(), car.getId());
            throw new ForbiddenActionException("The car does not belong to this driver");
        }
        if (request.proposedSeats() > car.getSize()) {
            logger.warn("Trip creation rejected: {} seats exceed car {} capacity {}",
                    request.proposedSeats(), car.getId(), car.getSize());
            throw new InvalidRequestException("Proposed seats cannot exceed car capacity");
        }
        validateStopPoints(request);
        Trip trip = tripMapper.toEntity(request);
        trip.setCar(car);
        trip.setDriver(car.getDriver());
        addStopPoints(trip, request);

        Trip savedTrip = tripRepository.save(trip);
        logger.info("Trip {} published by driver {} with car {}", savedTrip.getId(), savedTrip.getDriver().getId(),
                savedTrip.getCar().getId());
        return toResponse(savedTrip);
    }

    @Override
    public List<TripResponseDto> getAll() {
        return tripRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public TripResponseDto getById(Long id) {
        Trip trip = tripRepository.findDetailById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        return toResponse(trip);
    }

    @Override
    public TripResponseDto update(Long id, TripRequestDto request) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        Car car = carRepository.findById(request.carId()).orElseThrow(() -> new NotFoundException("Car with id : " + request.carId() + " not found"));
        if (trip.getStatus() == StatutTrajet.TERMINE || trip.getStatus() == StatutTrajet.ANNULE) {
            logger.warn("Trip update rejected: trip {} is already in final state {}", id, trip.getStatus());
            throw new ConflictException("A finished or cancelled trip cannot be changed");
        }
        if (!car.getDriver().getId().equals(request.driverId())) {
            logger.warn("Trip update rejected: driver {} does not own car {}", request.driverId(), car.getId());
            throw new ForbiddenActionException("The car does not belong to this driver");
        }
        if (request.proposedSeats() > car.getSize()) {
            logger.warn("Trip update rejected: {} seats exceed car {} capacity {}",
                    request.proposedSeats(), car.getId(), car.getSize());
            throw new InvalidRequestException("Proposed seats cannot exceed car capacity");
        }
        validateStopPoints(request);

        tripMapper.updateEntity(request, trip);
        trip.setCar(car);
        trip.setDriver(car.getDriver());
        trip.replaceStopPoints(new ArrayList<>());
        addStopPoints(trip, request);
        trip.refreshRouteSummary();

        Trip updatedTrip = tripRepository.save(trip);
        logger.info("Trip {} updated", updatedTrip.getId());
        return toResponse(updatedTrip);
    }

    @Override
    public List<TripResponseDto> getByDriverId(Long driverId) {
        return tripRepository.findByCarDriverId(driverId).stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<TripSearchResultDto> search(String departureCity, String arrivalCity, LocalDate date,
                                                     Integer seats, BigDecimal maxPrice, int page, int size) {
        if (page < 0 || size < 1) {
            logger.warn("Trip search rejected: invalid page {} or size {}", page, size);
            throw new InvalidRequestException("Page must be zero or greater and size must be positive");
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        int requestedSeats = seats == null ? 1 : seats;
        if (requestedSeats < 1) {
            logger.warn("Trip search rejected: requested seat count {} is invalid", requestedSeats);
            throw new InvalidRequestException("At least one seat must be requested");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Trip search rejected: maximum price cannot be negative");
            throw new InvalidRequestException("Maximum price cannot be negative");
        }
        Page<TripSearchResultDto> resultPage = tripRepository.search(StatutTrajet.PUBLIE,
                StatutReservation.CONFIRMEE, startOfDay, endOfDay,
                departureCity.toLowerCase(Locale.ROOT), arrivalCity.toLowerCase(Locale.ROOT),
                requestedSeats, maxPrice, PageRequest.of(page, size));
        List<TripSearchResultDto> content = resultPage.getContent().stream()
                .map(result -> new TripSearchResultDto(result.tripId(), result.driverId(), result.tripDepartureCity(),
                        result.tripArrivalCity(), result.tripDepartureDateTime(), result.boardingStopPointId(),
                        result.boardingCity(), result.boardingTime(), result.alightingStopPointId(),
                        result.alightingCity(), result.alightingTime(),
                        result.segmentPrice().setScale(2, java.math.RoundingMode.HALF_UP), result.remainingSeats()))
                .toList();
        return new PageResponse<>(content, resultPage.getNumber(), resultPage.getSize(),
                resultPage.getTotalElements(), resultPage.getTotalPages());
    }

    @Override
    public void complete(Long id, Long driverId) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));

        if (trip.getStatus() != StatutTrajet.PUBLIE && trip.getStatus() != StatutTrajet.COMPLET) {
            logger.warn("Trip completion rejected: trip {} has status {}", id, trip.getStatus());
            throw new ConflictException("Only a published or full trip can be completed");
        }
        checkOwner(trip, driverId);
        if (trip.getDepartureDateTime().isAfter(LocalDateTime.now())) {
            logger.warn("Trip completion rejected: trip {} has not departed", id);
            throw new ConflictException("A trip can only be completed after its departure time");
        }

        trip.setStatus(StatutTrajet.TERMINE);
        tripRepository.save(trip);
        logger.info("Trip {} marked as completed by driver {}", id, driverId);
    }

    @Override
    public void cancel(Long id, Long driverId) {
        Trip trip = tripRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));

        if (trip.getStatus() != StatutTrajet.PUBLIE && trip.getStatus() != StatutTrajet.COMPLET) {
            logger.warn("Trip cancellation rejected: trip {} has status {}", id, trip.getStatus());
            throw new ConflictException("Only a published or full trip can be cancelled");
        }
        checkOwner(trip, driverId);
        reservationRepository.cancelConfirmedByTripId(trip.getId(), StatutReservation.CONFIRMEE,
                StatutReservation.ANNULEE);

        trip.setStatus(StatutTrajet.ANNULE);
        tripRepository.save(trip);
        logger.info("Trip {} cancelled by driver {} and confirmed reservations cancelled", id, driverId);
    }

    @Override
    public void delete(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        if (reservationRepository.existsByTripId(id)) {
            logger.warn("Trip deletion rejected: trip {} has reservations", id);
            throw new ConflictException("A trip with reservations cannot be deleted");
        }
        tripRepository.delete(trip);
        logger.info("Trip {} deleted", id);
    }

    private TripResponseDto toResponse(Trip trip) {
        Long reservedSeats = reservationRepository.sumSeatsByTripAndStatus(trip.getId(), StatutReservation.CONFIRMEE);
        return tripMapper.toResponse(trip, reservedSeats);
    }

    private void addStopPoints(Trip trip, TripRequestDto request) {
        for (var stopPointRequest : request.stopPoints()) {
            StopPoint stopPoint = stopPointMapper.toEntity(stopPointRequest);
            trip.addStopPoint(stopPoint);
        }
        trip.refreshRouteSummary();
    }

    private void validateStopPoints(TripRequestDto request) {
        List<String> cities = new ArrayList<>();
        LocalDateTime previousTime = null;
        BigDecimal previousPrice = null;
        for (int i = 0; i < request.stopPoints().size(); i++) {
            var stop = request.stopPoints().get(i);
            String city = stop.city().trim();
            if (cities.contains(city.toLowerCase(Locale.ROOT))) {
                logger.warn("Trip rejected: city {} appears more than once", city);
                throw new ConflictException("A city cannot appear twice in a trip");
            }
            cities.add(city.toLowerCase(Locale.ROOT));
            if (previousTime != null && !stop.passageTime().isAfter(previousTime)) {
                logger.warn("Trip rejected: stop times are not increasing");
                throw new ConflictException("Stop times must be in increasing order");
            }
            if (i > 0 && stop.stopOrder() <= request.stopPoints().get(i - 1).stopOrder()) {
                logger.warn("Trip rejected: stop order values are not increasing");
                throw new ConflictException("Stop order values must increase along the trip");
            }
            if (i == 0 && stop.priceFromDeparture().compareTo(BigDecimal.ZERO) != 0) {
                logger.warn("Trip rejected: departure stop price must be zero");
                throw new ConflictException("The departure stop price must be zero");
            }
            if (previousPrice != null && stop.priceFromDeparture().compareTo(previousPrice) <= 0) {
                logger.warn("Trip rejected: stop prices are not increasing");
                throw new ConflictException("Stop prices must increase along the trip");
            }
            previousTime = stop.passageTime();
            previousPrice = stop.priceFromDeparture();
        }
        if (!request.stopPoints().get(0).passageTime().isAfter(LocalDateTime.now())) {
            logger.warn("Trip rejected: departure time is not in the future");
            throw new ConflictException("Departure time must be in the future");
        }
    }

    private void checkOwner(Trip trip, Long driverId) {
        if (trip.getDriver() == null || !trip.getDriver().getId().equals(driverId)) {
            logger.warn("Trip ownership check rejected driver {} for trip {}", driverId, trip.getId());
            throw new ForbiddenActionException("This trip does not belong to this driver");
        }
    }
}
