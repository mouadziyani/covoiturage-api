package ma.youcode.covoiturageapi.reservation.repository;

import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByTripIdAndPassagerIdAndStatus(Long tripId, Long passagerId, StatutReservation status);

    List<Reservation> findByPassagerIdOrderByReservationDateDesc(Long passagerId);

    List<Reservation> findByTripDriverIdOrderByReservationDateDesc(Long driverId);

    Optional<Reservation> findByIdAndPassagerId(Long id, Long passagerId);

    @Query("select coalesce(sum(r.numberOfSeats), 0) from Reservation r where r.trip.id = :tripId and r.status = :status")
    Long sumSeatsByTripAndStatus(@Param("tripId") Long tripId, @Param("status") StatutReservation status);

    boolean existsByTripId(Long tripId);

    @Modifying
    @Query("update Reservation r set r.status = :cancelled where r.trip.id = :tripId and r.status = :confirmed")
    int cancelConfirmedByTripId(@Param("tripId") Long tripId,
                                @Param("confirmed") StatutReservation confirmed,
                                @Param("cancelled") StatutReservation cancelled);
}
