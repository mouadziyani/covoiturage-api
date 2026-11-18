package ma.youcode.covoiturageapi.passager.dto.response;

public record PassagerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
