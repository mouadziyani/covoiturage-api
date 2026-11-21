package ma.youcode.covoiturageapi.trip.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ma.youcode.covoiturageapi.stoppoint.dto.request.StopPointRequestDto;

import java.util.List;

public record TripRequestDto(
        @NotNull(message = "Driver ID is required")
        Long driverId,
        @NotNull(message = "Car ID is required")
        Long carId,
        @NotNull(message = "Proposed seats are required")
        @Min(value = 1, message = "At least one seat must be proposed")
        Integer proposedSeats,
        @NotEmpty(message = "At least two stop points are required")
        @Size(min = 2, message = "A trip must have at least two stop points")
        List<@Valid StopPointRequestDto> stopPoints
) {
}
