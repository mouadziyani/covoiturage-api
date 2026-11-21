package ma.youcode.covoiturageapi.stoppoint.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.covoiturageapi.trip.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stop_points", uniqueConstraints = {
        @UniqueConstraint(name = "uk_stop_point_trip_order", columnNames = {"trip_id", "stop_order"})
})
@Getter
@Setter
@NoArgsConstructor
public class StopPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private String city;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(nullable = false)
    private LocalDateTime passageTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceFromDeparture;
}
