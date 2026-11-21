package ma.youcode.covoiturageapi.trip.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.driver.entity.Driver;
import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver   driver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String arrivalCity;

    @Column(nullable = false)
    private LocalDateTime departureDateTime;

    @Column(nullable = false)
    private Integer proposedSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTrajet status = StatutTrajet.PUBLIE;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    private List<StopPoint> stopPoints = new ArrayList<>();

    public void addStopPoint(StopPoint stopPoint) {
        stopPoints.add(stopPoint);
        stopPoint.setTrip(this);
    }

    public void replaceStopPoints(List<StopPoint> newStopPoints) {
        stopPoints.clear();
        newStopPoints.forEach(this::addStopPoint);
    }

    public void refreshRouteSummary() {
        if (stopPoints.size() >= 2) {
            departureCity = stopPoints.get(0).getCity();
            arrivalCity = stopPoints.get(stopPoints.size() - 1).getCity();
            departureDateTime = stopPoints.get(0).getPassageTime();
        }
    }
}
