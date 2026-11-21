package ma.youcode.covoiturageapi.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.covoiturageapi.enums.StatutReservation;
import ma.youcode.covoiturageapi.passager.entity.Passager;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import ma.youcode.covoiturageapi.trip.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boarding_stop_point_id", nullable = false)
    private StopPoint boardingStopPoint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alighting_stop_point_id", nullable = false)
    private StopPoint alightingStopPoint;

    @Column(nullable = false)
    private Integer numberOfSeats;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime reservationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation status = StatutReservation.CONFIRMEE;
}
