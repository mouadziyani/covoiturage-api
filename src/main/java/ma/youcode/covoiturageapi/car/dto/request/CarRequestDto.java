package ma.youcode.covoiturageapi.car.dto.request;

import jakarta.validation.constraints.NotBlank;
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
        @NotBlank(message = "Size is required")
        String size,
        @NotNull(message = "Driver ID is required")
        Long driverId) {
}
