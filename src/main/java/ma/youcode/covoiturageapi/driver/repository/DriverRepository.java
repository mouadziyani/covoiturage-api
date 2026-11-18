package ma.youcode.covoiturageapi.driver.repository;

<<<<<<< Updated upstream
<<<<<<< Updated upstream
public class DriverRepository {
=======
import ma.youcode.covoiturageapi.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {


>>>>>>> Stashed changes
=======
import ma.youcode.covoiturageapi.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByEmail(String email);

    boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDrivingLicenseNumberAndIdNot(String drivingLicenseNumber, Long id);

    List<Driver> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
>>>>>>> Stashed changes
}
