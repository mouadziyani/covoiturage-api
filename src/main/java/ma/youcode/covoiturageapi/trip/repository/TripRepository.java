package ma.youcode.covoiturageapi.trip.repository;

import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.dto.response.TripSearchResultDto;
import ma.youcode.covoiturageapi.enums.StatutReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Trip t where t.id = :tripId")
    Optional<Trip> findByIdForUpdate(@Param("tripId") Long tripId);

    List<Trip> findByCarId(Long carId);

    boolean existsByCarId(Long carId);

    List<Trip> findByCarDriverId(Long driverId);

    List<Trip> findByStatus(StatutTrajet status);

    List<Trip> findByDepartureCityIgnoreCaseAndArrivalCityIgnoreCaseAndDepartureDateTimeBetween(
            String departureCity,
            String arrivalCity,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    @Query(value = "select new ma.youcode.covoiturageapi.trip.dto.response.TripSearchResultDto( " +
            "t.id, d.id, t.departureCity, t.arrivalCity, t.departureDateTime, " +
            "dep.id, dep.city, dep.passageTime, arr.id, arr.city, arr.passageTime, " +
            "(arr.priceFromDeparture - dep.priceFromDeparture), " +
            "(t.proposedSeats - coalesce((select sum(r.numberOfSeats) from Reservation r " +
            "where r.trip.id = t.id and r.status = :confirmedStatus), 0L))) " +
            "from Trip t join t.driver d join t.stopPoints dep join t.stopPoints arr " +
            "where t.status = :publishedStatus " +
            "and t.departureDateTime >= :startDateTime and t.departureDateTime < :endDateTime " +
            "and lower(dep.city) = :departureCity and lower(arr.city) = :arrivalCity " +
            "and dep.stopOrder < arr.stopOrder " +
            "and (t.proposedSeats - coalesce((select sum(r.numberOfSeats) from Reservation r " +
            "where r.trip.id = t.id and r.status = :confirmedStatus), 0L)) >= :requestedSeats " +
            "and (:maxPrice is null or (arr.priceFromDeparture - dep.priceFromDeparture) <= :maxPrice) " +
            "order by t.departureDateTime asc",
            countQuery = "select count(distinct t.id) " +
                    "from Trip t join t.stopPoints dep join t.stopPoints arr " +
                    "where t.status = :publishedStatus " +
                    "and t.departureDateTime >= :startDateTime and t.departureDateTime < :endDateTime " +
                    "and lower(dep.city) = :departureCity and lower(arr.city) = :arrivalCity " +
                    "and dep.stopOrder < arr.stopOrder " +
                    "and (t.proposedSeats - coalesce((select sum(r.numberOfSeats) from Reservation r " +
                    "where r.trip.id = t.id and r.status = :confirmedStatus), 0L)) >= :requestedSeats " +
                    "and (:maxPrice is null or (arr.priceFromDeparture - dep.priceFromDeparture) <= :maxPrice)")
    Page<TripSearchResultDto> search(@Param("publishedStatus") StatutTrajet publishedStatus,
                                     @Param("confirmedStatus") StatutReservation confirmedStatus,
                                     @Param("startDateTime") LocalDateTime startDateTime,
                                     @Param("endDateTime") LocalDateTime endDateTime,
                                     @Param("departureCity") String departureCity,
                                     @Param("arrivalCity") String arrivalCity,
                                     @Param("requestedSeats") Integer requestedSeats,
                                     @Param("maxPrice") BigDecimal maxPrice,
                                     Pageable pageable);
}
