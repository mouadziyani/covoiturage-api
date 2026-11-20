package ma.youcode.covoiturageapi.trip.dto.response;

import ma.youcode.covoiturageapi.enums.StatutTrajet;

import java.time.LocalDateTime;

public record TripResponseDto(
        Long id,
        Long carId,
        String departureCity,
        String arrivalCity,
        LocalDateTime departureDateTime,
        Integer proposedSeats,
        StatutTrajet status
) {
}
