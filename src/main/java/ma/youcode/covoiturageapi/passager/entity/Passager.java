package ma.youcode.covoiturageapi.passager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.covoiturageapi.user.entity.User;

@Entity
@Table(name = "passagers")
@Getter
@Setter
<<<<<<< Updated upstream
@NoArgsConstructor
=======
>>>>>>> Stashed changes
public class Passager extends User {
}
