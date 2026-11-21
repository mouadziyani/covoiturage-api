package ma.youcode.covoiturageapi.reservation.dto.response;

import ma.youcode.covoiturageapi.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDto(
        Long id,
        Long passagerId,
        Long tripId,
        Long boardingStopPointId,
        String boardingCity,
        Long alightingStopPointId,
        String alightingCity,
        Integer numberOfSeats,
        BigDecimal totalAmount,
        LocalDateTime reservationDate,
        StatutReservation status
) {
}
