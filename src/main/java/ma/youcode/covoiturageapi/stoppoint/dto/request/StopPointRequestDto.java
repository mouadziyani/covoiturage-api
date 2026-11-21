package ma.youcode.covoiturageapi.stoppoint.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StopPointRequestDto(
        @NotBlank(message = "Stop city is required")
        String city,
        @NotNull(message = "Stop order is required")
        Integer stopOrder,
        @NotNull(message = "Passage time is required")
        @Future(message = "Passage time must be in the future")
        LocalDateTime passageTime,
        @NotNull(message = "Price from departure is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most two decimal places")
        BigDecimal priceFromDeparture
) {
}
