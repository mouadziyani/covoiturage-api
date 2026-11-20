package ma.youcode.covoiturageapi.car.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import ma.youcode.covoiturageapi.driver.entity.Driver;

@Entity
@Table(name = "cars")
@Getter @Setter
public class Car {
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
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 8, message = "Capacity must not exceed 8")
    private int size;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}
