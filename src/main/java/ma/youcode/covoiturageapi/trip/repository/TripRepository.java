package ma.youcode.covoiturageapi.trip.repository;

import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCarId(Long carId);

    List<Trip> findByCarDriverId(Long driverId);

    List<Trip> findByStatus(StatutTrajet status);

    List<Trip> findByDepartureCityIgnoreCaseAndArrivalCityIgnoreCaseAndDepartureDateTimeBetween(
            String departureCity,
            String arrivalCity,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
