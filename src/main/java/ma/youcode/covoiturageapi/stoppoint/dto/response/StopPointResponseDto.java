package ma.youcode.covoiturageapi.stoppoint.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StopPointResponseDto(
        Long id,
        Long tripId,
        String city,
        Integer stopOrder,
        LocalDateTime passageTime,
        BigDecimal priceFromDeparture
) {
}
