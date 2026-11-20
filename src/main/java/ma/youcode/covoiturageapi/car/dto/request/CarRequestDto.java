package ma.youcode.covoiturageapi.car.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarRequestDto(
        @NotBlank(message = "Brand is required")
        String brand,
        @NotBlank(message = "Model is required")
        String model,
        @NotBlank(message = "Registration number is required")
        String registrationNumber,
        @NotBlank(message = "Color is required")
        String color,
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 8, message = "Capacity must not exceed 8")
        Integer size,
        @NotNull(message = "Driver ID is required")
        Long driverId) {
}
