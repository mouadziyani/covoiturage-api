package ma.youcode.covoiturageapi.driver.dto.response;

public record DriverResponseDto(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String drivingLicenseNumber
){
}
