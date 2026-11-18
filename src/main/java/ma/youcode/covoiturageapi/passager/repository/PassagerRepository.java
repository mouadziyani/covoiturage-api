package ma.youcode.covoiturageapi.passager.repository;

import ma.youcode.covoiturageapi.passager.entity.Passager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassagerRepository extends JpaRepository<Passager, Long> {
    boolean existsByEmail(String email);

    Optional<Passager> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);


}
