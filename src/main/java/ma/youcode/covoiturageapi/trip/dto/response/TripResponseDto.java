package ma.youcode.covoiturageapi.trip.dto.response;

import ma.youcode.covoiturageapi.enums.StatutTrajet;
import ma.youcode.covoiturageapi.stoppoint.dto.response.StopPointResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record TripResponseDto(
        Long id,
        Long carId,
        Long driverId,
        String departureCity,
        String arrivalCity,
        LocalDateTime departureDateTime,
        Integer proposedSeats,
        Integer remainingSeats,
        StatutTrajet status,
        List<StopPointResponseDto> stopPoints
) {
}
