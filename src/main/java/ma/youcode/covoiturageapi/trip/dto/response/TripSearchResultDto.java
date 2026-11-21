package ma.youcode.covoiturageapi.trip.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripSearchResultDto(
        Long tripId,
        Long driverId,
        String tripDepartureCity,
        String tripArrivalCity,
        LocalDateTime tripDepartureDateTime,
        Long boardingStopPointId,
        String boardingCity,
        LocalDateTime boardingTime,
        Long alightingStopPointId,
        String alightingCity,
        LocalDateTime alightingTime,
        BigDecimal segmentPrice,
        Long remainingSeats
) {
}
