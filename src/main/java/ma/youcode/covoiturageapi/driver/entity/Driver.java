package ma.youcode.covoiturageapi.driver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import ma.youcode.covoiturageapi.user.entity.User;

@Entity
@Getter @Setter
public class Driver extends User {
    @Column(unique = true,nullable = false)
    private String drivingLicenseNumber;
}
