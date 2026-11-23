package ma.youcode.covoiturageapi.trip.service.impl;

import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.car.repository.CarRepository;
import ma.youcode.covoiturageapi.driver.entity.Driver;
import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.ForbiddenActionException;
import ma.youcode.covoiturageapi.exception.InvalidRequestException;
import ma.youcode.covoiturageapi.reservation.repository.ReservationRepository;
import ma.youcode.covoiturageapi.stoppoint.dto.request.StopPointRequestDto;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import ma.youcode.covoiturageapi.stoppoint.mapper.StopPointMapper;
import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.mapper.TripMapper;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceImpTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private TripMapper tripMapper;
    @Mock
    private StopPointMapper stopPointMapper;
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private TripServiceImp tripService;
    private Driver driver(Long id) {
        Driver driver = new Driver();
        driver.setId(id);
        return driver;
    }

    private Car car(Long id, Driver owner, int size) {
        Car car = new Car();
        car.setId(id);
        car.setDriver(owner);
        car.setSize(size);
        return car;
    }

    private StopPointRequestDto stop(String city, int order, int hoursFromNow, String price) {
        return new StopPointRequestDto(city, order, LocalDateTime.now().plusHours(hoursFromNow), new BigDecimal(price));
    }
    private TripRequestDto request(Long driverId, Long carId, int seats) {
        return new TripRequestDto(driverId, carId, seats, List.of(
                stop("Rabat", 0, 48, "0"),
                stop("Marrakech", 1, 52, "120")));
    }

    private Trip trip(Long id, Driver driver, StatutTrajet status, LocalDateTime departure) {
        Trip trip = new Trip();
        trip.setId(id);
        trip.setDriver(driver);
        trip.setStatus(status);
        trip.setDepartureDateTime(departure);
        return trip;
    }
    @Test
    void createSavesTripWhenEverythingIsValid() {
        Driver hamid = driver(1L);
        when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, hamid, 4)));
        when(tripMapper.toEntity(any())).thenReturn(new Trip());
        when(stopPointMapper.toEntity(any())).thenAnswer(invocation -> new StopPoint());
        when(tripRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        tripService.create(request(1L, 5L, 3));

        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void createRejectsCarOfAnotherDriver() {
        Driver otherDriver = driver(2L);
        when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, otherDriver, 4)));

        assertThatThrownBy(() -> tripService.create(request(1L, 5L, 3)))
                .isInstanceOf(ForbiddenActionException.class);

        verify(tripRepository, never()).save(any());
    }

    @Test
    void createRejectsMoreSeatsThanCarCapacity() {
        Driver hamid = driver(1L);
        when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, hamid, 4)));

        assertThatThrownBy(() -> tripService.create(request(1L, 5L, 5)))
                .isInstanceOf(InvalidRequestException.class);

        verify(tripRepository, never()).save(any());
    }

    @Test
    void createRejectsSameCityTwice() {
        Driver hamid = driver(1L);
        when(carRepository.findById(5L)).thenReturn(Optional.of(car(5L, hamid, 4)));
        TripRequestDto badRequest = new TripRequestDto(1L, 5L, 3, List.of(
                stop("Rabat", 0, 48, "0"),
                stop("Rabat", 1, 50, "40")));

        assertThatThrownBy(() -> tripService.create(badRequest))
                .isInstanceOf(ConflictException.class);
    }
    @Test
    void completeRejectsTripBeforeDeparture() {
        Trip trip = trip(10L, driver(1L), StatutTrajet.PUBLIE, LocalDateTime.now().plusHours(3));
        when(tripRepository.findById(10L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> tripService.complete(10L, 1L))
                .isInstanceOf(ConflictException.class);

        assertThat(trip.getStatus()).isEqualTo(StatutTrajet.PUBLIE);
    }
    @Test
    void cancelCancelsTripAndItsReservations() {
        Trip trip = trip(10L, driver(1L), StatutTrajet.PUBLIE, LocalDateTime.now().plusDays(3));
        when(tripRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trip));

        tripService.cancel(10L, 1L);

        assertThat(trip.getStatus()).isEqualTo(StatutTrajet.ANNULE);
        verify(reservationRepository).cancelConfirmedByTripId(10L,
                StatutReservation.CONFIRMEE, StatutReservation.ANNULEE);
    }

    @Test
    void cancelRejectsTripOfAnotherDriver() {
        Trip trip = trip(10L, driver(1L), StatutTrajet.PUBLIE, LocalDateTime.now().plusDays(3));
        when(tripRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> tripService.cancel(10L, 2L))
                .isInstanceOf(ForbiddenActionException.class);

        assertThat(trip.getStatus()).isEqualTo(StatutTrajet.PUBLIE);
    }
}