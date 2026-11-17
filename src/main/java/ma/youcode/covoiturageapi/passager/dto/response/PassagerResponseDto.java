package ma.youcode.covoiturageapi.passager.dto.response;

public record PassagerResponseDto(
        string id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
