package ma.youcode.covoiturageapi.car.dto.response;

public record CarResponseDto(
        Long id,
        String brand,
        String model,
        String registrationNumber,
        String color,
        String size,
        Long driverId
) {
}
