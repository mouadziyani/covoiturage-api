package ma.youcode.covoiturageapi.trip.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TripRequestDto(
        @NotNull(message = "Car ID is required")
        Long carId,
        @NotBlank(message = "Departure city is required")
        String departureCity,
        @NotBlank(message = "Arrival city is required")
        String arrivalCity,
        @NotNull(message = "Departure date and time are required")
        @Future(message = "Departure must be in the future")
        LocalDateTime departureDateTime,
        @NotNull(message = "Proposed seats are required")
        @Min(value = 1, message = "At least one seat must be proposed")
        Integer proposedSeats
) {
}
