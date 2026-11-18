package ma.youcode.covoiturageapi.vehicule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ma.youcode.covoiturageapi.driver.entity.Driver;

@Entity
@Table(name = "vehicules")
@Getter @Setter
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String model;
    @Column(unique = true, nullable = false)
    private String registrationNumber;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String size;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}
