package ma.youcode.covoiturageapi.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestDto(
        @NotNull(message = "Passenger ID is required")
        Long passagerId,
        @NotNull(message = "Trip ID is required")
        Long tripId,
        @NotNull(message = "Boarding stop point ID is required")
        Long boardingStopPointId,
        @NotNull(message = "Destination stop point ID is required")
        Long alightingStopPointId,
        @NotNull(message = "Number of seats is required")
        @Min(value = 1, message = "At least one seat must be reserved")
        Integer numberOfSeats
) {
}
