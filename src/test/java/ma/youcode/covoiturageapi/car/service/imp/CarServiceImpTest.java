package ma.youcode.covoiturageapi.car.service.imp;

import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.car.mappper.CarMapper;
import ma.youcode.covoiturageapi.car.repository.CarRepository;
import ma.youcode.covoiturageapi.driver.repository.DriverRepository;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImpTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private TripRepository tripRepository;

    private CarServiceImp carService;

    @BeforeEach
    void setUp() {
        carService = new CarServiceImp(carRepository, driverRepository, carMapper, tripRepository);
    }

    @Test
    void deleteRejectsCarThatIsUsedByATrip() {
        Car car = new Car();
        car.setId(7L);
        when(carRepository.findById(7L)).thenReturn(Optional.of(car));
        when(tripRepository.existsByCarId(7L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> carService.delete(7L));

        verify(carRepository, never()).delete(car);
    }

    @Test
    void deleteRemovesCarWhenNoTripUsesIt() {
        Car car = new Car();
        car.setId(7L);
        when(carRepository.findById(7L)).thenReturn(Optional.of(car));
        when(tripRepository.existsByCarId(7L)).thenReturn(false);

        carService.delete(7L);

        verify(carRepository).delete(car);
    }
}
