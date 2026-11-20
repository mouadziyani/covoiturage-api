package ma.youcode.covoiturageapi.car.repository;

import ma.youcode.covoiturageapi.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);
    List<Car> findByDriverId(Long driverId);
}
