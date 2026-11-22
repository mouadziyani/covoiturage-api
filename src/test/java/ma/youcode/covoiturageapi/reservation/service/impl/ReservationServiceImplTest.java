package ma.youcode.covoiturageapi.reservation.service.impl;

import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.ForbiddenActionException;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.exception.InvalidRequestException;
import ma.youcode.covoiturageapi.passager.entity.Passager;
import ma.youcode.covoiturageapi.passager.repository.PassagerRepository;
import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;
import ma.youcode.covoiturageapi.reservation.entity.Reservation;
import ma.youcode.covoiturageapi.reservation.mapper.ReservationMapper;
import ma.youcode.covoiturageapi.reservation.repository.ReservationRepository;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import ma.youcode.covoiturageapi.stoppoint.repository.StopPointRepository;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private PassagerRepository passagerRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private StopPointRepository stopPointRepository;
    @Mock
    private ReservationMapper reservationMapper;

    private ReservationServiceImpl reservationService;
    private Passager passager;
    private Trip trip;
    private StopPoint boarding;
    private StopPoint destination;
    private ReservationRequestDto request;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(reservationRepository, passagerRepository, tripRepository,
                stopPointRepository, reservationMapper);

        passager = new Passager();
        passager.setId(10L);

        trip = new Trip();
        trip.setId(20L);
        trip.setProposedSeats(2);
        trip.setStatus(StatutTrajet.PUBLIE);
        trip.setDepartureDateTime(LocalDateTime.now().plusDays(3));

        boarding = new StopPoint();
        boarding.setId(30L);
        boarding.setTrip(trip);
        boarding.setStopOrder(1);
        boarding.setPriceFromDeparture(new BigDecimal("0.00"));

        destination = new StopPoint();
        destination.setId(31L);
        destination.setTrip(trip);
        destination.setStopOrder(2);
        destination.setPriceFromDeparture(new BigDecimal("12.50"));

        request = new ReservationRequestDto(10L, 20L, 30L, 31L, 2);
        lenient().when(passagerRepository.findById(10L)).thenReturn(Optional.of(passager));
        lenient().when(tripRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trip));
        lenient().when(stopPointRepository.findById(30L)).thenReturn(Optional.of(boarding));
        lenient().when(stopPointRepository.findById(31L)).thenReturn(Optional.of(destination));
        lenient().when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(0L);
        lenient().when(reservationRepository.existsByTripIdAndPassagerIdAndStatus(20L, 10L, StatutReservation.CONFIRMEE))
                .thenReturn(false);
        lenient().when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(reservationMapper.toEntity(eq(request), eq(passager), eq(trip), eq(boarding), eq(destination), any()))
                .thenAnswer(invocation -> new Reservation());
        lenient().when(reservationMapper.toResponse(any(Reservation.class))).thenReturn(response());
    }

    @Test
    void createCalculatesAmountAndMarksTripFullWhenAllSeatsAreReserved() {
        reservationService.create(request);

        ArgumentCaptor<BigDecimal> amount = ArgumentCaptor.forClass(BigDecimal.class);
        verify(reservationMapper).toEntity(eq(request), eq(passager), eq(trip), eq(boarding), eq(destination), amount.capture());
        assertEquals(new BigDecimal("25.00"), amount.getValue());
        assertEquals(StatutTrajet.COMPLET, trip.getStatus());
        verify(tripRepository).save(trip);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createRejectsTripThatIsNotPublished() {
        trip.setStatus(StatutTrajet.COMPLET);

        assertThrows(ConflictException.class, () -> reservationService.create(request));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createRejectsStopPointFromAnotherTrip() {
        Trip anotherTrip = new Trip();
        anotherTrip.setId(99L);
        destination.setTrip(anotherTrip);

        assertThrows(ConflictException.class, () -> reservationService.create(request));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createRejectsBoardingStopThatComesAfterDestination() {
        boarding.setStopOrder(3);

        assertThrows(InvalidRequestException.class, () -> reservationService.create(request));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createRejectsWhenThereAreNotEnoughSeats() {
        when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(1L);

        assertThrows(ConflictException.class, () -> reservationService.create(request));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createRejectsSecondConfirmedReservationForSamePassengerAndTrip() {
        when(reservationRepository.existsByTripIdAndPassagerIdAndStatus(20L, 10L, StatutReservation.CONFIRMEE))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> reservationService.create(request));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void cancelMarksReservationCancelledAndReopensTrip() {
        Reservation reservation = new Reservation();
        reservation.setId(40L);
        reservation.setPassager(passager);
        reservation.setTrip(trip);
        reservation.setStatus(StatutReservation.CONFIRMEE);
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));
        trip.setStatus(StatutTrajet.COMPLET);
        when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(0L);

        reservationService.cancel(40L, 10L);

        assertEquals(StatutReservation.ANNULEE, reservation.getStatus());
        assertEquals(StatutTrajet.PUBLIE, trip.getStatus());
        verify(reservationRepository).save(reservation);
        verify(tripRepository).save(trip);
    }

    @Test
    void cancelRejectsReservationWithin24HoursOfDeparture() {
        Reservation reservation = new Reservation();
        reservation.setId(40L);
        reservation.setPassager(passager);
        reservation.setTrip(trip);
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));
        trip.setDepartureDateTime(LocalDateTime.now().plusHours(23));

        assertThrows(ConflictException.class, () -> reservationService.cancel(40L, 10L));

        verify(reservationRepository, never()).save(reservation);
    }

    @Test
    void cancelReturnsNotFoundWhenReservationDoesNotExist() {
        when(reservationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservationService.cancel(404L, 10L));
    }

    @Test
    void cancelReturnsForbiddenWhenReservationBelongsToAnotherPassenger() {
        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));

        assertThrows(ForbiddenActionException.class, () -> reservationService.cancel(40L, 999L));
    }

    @Test
    void cancelReturnsConflictWhenReservationIsAlreadyCancelled() {
        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        reservation.setStatus(StatutReservation.ANNULEE);
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));

        assertThrows(ConflictException.class, () -> reservationService.cancel(40L, 10L));
    }

    @Test
    void getByIdReturnsForbiddenWhenReservationBelongsToAnotherPassenger() {
        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));

        assertThrows(ForbiddenActionException.class, () -> reservationService.getById(40L, 999L));
    }

    private ReservationResponseDto response() {
        return new ReservationResponseDto(40L, 10L, 20L, 30L, "Start", 31L, "End", 2,
                new BigDecimal("25.00"), LocalDateTime.now(), StatutReservation.CONFIRMEE);
    }
}
