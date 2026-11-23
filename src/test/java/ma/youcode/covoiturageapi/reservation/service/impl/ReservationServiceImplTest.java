package ma.youcode.covoiturageapi.reservation.service.impl;

import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.ForbiddenActionException;
import ma.youcode.covoiturageapi.exception.InvalidRequestException;
import ma.youcode.covoiturageapi.passager.entity.Passager;
import ma.youcode.covoiturageapi.passager.repository.PassagerRepository;
import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Passager salma;
    private Trip trip;
    private StopPoint settat;
    private StopPoint marrakech;

    @BeforeEach
    void setUp() {
        salma = new Passager();
        salma.setId(10L);

        trip = new Trip();
        trip.setId(20L);
        trip.setProposedSeats(2);
        trip.setStatus(StatutTrajet.PUBLIE);
        trip.setDepartureDateTime(LocalDateTime.now().plusDays(3));

        settat = new StopPoint();
        settat.setId(30L);
        settat.setTrip(trip);
        settat.setStopOrder(2);
        settat.setPriceFromDeparture(new BigDecimal("70"));

        marrakech = new StopPoint();
        marrakech.setId(31L);
        marrakech.setTrip(trip);
        marrakech.setStopOrder(3);
        marrakech.setPriceFromDeparture(new BigDecimal("120"));
    }
    private ReservationRequestDto request(int seats) {
        return new ReservationRequestDto(10L, 20L, 30L, 31L, seats);
    }
    private void stubPassengerTripAndStops() {
        when(passagerRepository.findById(10L)).thenReturn(Optional.of(salma));
        when(tripRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trip));
        when(stopPointRepository.findById(30L)).thenReturn(Optional.of(settat));
        when(stopPointRepository.findById(31L)).thenReturn(Optional.of(marrakech));
    }
    private Reservation confirmedReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(40L);
        reservation.setPassager(salma);
        reservation.setTrip(trip);
        reservation.setStatus(StatutReservation.CONFIRMEE);
        return reservation;
    }
    @Test
    void createCalculatesAmountAndMarksTripFull() {
        stubPassengerTripAndStops();
        when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(0L);
        when(reservationMapper.toEntity(any(), any(), any(), any(), any(), any())).thenReturn(new Reservation());
        when(reservationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        reservationService.create(request(2));
        ArgumentCaptor<BigDecimal> amount = ArgumentCaptor.forClass(BigDecimal.class);
        verify(reservationMapper).toEntity(any(), any(), any(), any(), any(), amount.capture());
        assertThat(amount.getValue()).isEqualByComparingTo("100.00");
        assertThat(trip.getStatus()).isEqualTo(StatutTrajet.COMPLET);
    }
    @Test
    void createRejectsTripThatIsNotPublished() {
        trip.setStatus(StatutTrajet.COMPLET);
        when(passagerRepository.findById(10L)).thenReturn(Optional.of(salma));
        when(tripRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> reservationService.create(request(1)))
                .isInstanceOf(ConflictException.class);

        verify(reservationRepository, never()).save(any());
    }
    @Test
    void createRejectsBoardingAfterDestination() {
        settat.setStopOrder(5);
        stubPassengerTripAndStops();

        assertThatThrownBy(() -> reservationService.create(request(1)))
                .isInstanceOf(InvalidRequestException.class);

        verify(reservationRepository, never()).save(any());
    }
    @Test
    void createRejectsWhenNotEnoughSeats() {
        stubPassengerTripAndStops();
        when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(1L);

        assertThatThrownBy(() -> reservationService.create(request(2)))
                .isInstanceOf(ConflictException.class);

        verify(reservationRepository, never()).save(any());
    }
    @Test
    void cancelCancelsReservationAndReopensTrip() {
        trip.setStatus(StatutTrajet.COMPLET);
        Reservation reservation = confirmedReservation();
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));
        when(tripRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trip));
        when(reservationRepository.sumSeatsByTripAndStatus(20L, StatutReservation.CONFIRMEE)).thenReturn(0L);

        reservationService.cancel(40L, 10L);

        assertThat(reservation.getStatus()).isEqualTo(StatutReservation.ANNULEE);
        assertThat(trip.getStatus()).isEqualTo(StatutTrajet.PUBLIE);
    }
    @Test
    void cancelRejectsLessThan24HoursBeforeDeparture() {
        trip.setDepartureDateTime(LocalDateTime.now().plusHours(2));
        Reservation reservation = confirmedReservation();
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(reservation));
        when(tripRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> reservationService.cancel(40L, 10L))
                .isInstanceOf(ConflictException.class);

        assertThat(reservation.getStatus()).isEqualTo(StatutReservation.CONFIRMEE);
    }
    @Test
    void cancelRejectsReservationOfAnotherPassenger() {
        when(reservationRepository.findById(40L)).thenReturn(Optional.of(confirmedReservation()));

        assertThatThrownBy(() -> reservationService.cancel(40L, 999L))
                .isInstanceOf(ForbiddenActionException.class);
    }
}