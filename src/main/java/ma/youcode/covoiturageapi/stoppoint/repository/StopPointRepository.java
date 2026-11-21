package ma.youcode.covoiturageapi.stoppoint.repository;

import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StopPointRepository extends JpaRepository<StopPoint, Long> {
    List<StopPoint> findByTripIdOrderByStopOrderAsc(Long tripId);
}
