package ma.youcode.covoiturageapi.passager.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import ma.youcode.covoiturageapi.user.entity.User;

@Entity
@Getter @Setter
public class Passager extends User {
}
