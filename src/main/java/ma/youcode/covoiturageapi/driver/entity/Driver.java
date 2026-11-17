package ma.youcode.covoiturageapi.driver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
<<<<<<< Updated upstream
import lombok.Getter;
=======
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
>>>>>>> Stashed changes
import lombok.Setter;
import ma.youcode.covoiturageapi.user.entity.User;

@Entity
<<<<<<< Updated upstream
@Getter @Setter
=======
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
>>>>>>> Stashed changes
public class Driver extends User {
    @Column(unique = true,nullable = false)
    private String drivingLicenseNumber;
}
