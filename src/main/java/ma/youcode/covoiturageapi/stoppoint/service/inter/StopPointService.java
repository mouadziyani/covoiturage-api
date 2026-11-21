package ma.youcode.covoiturageapi.stoppoint.service.inter;

import ma.youcode.covoiturageapi.stoppoint.dto.response.StopPointResponseDto;

import java.util.List;

public interface StopPointService {
    List<StopPointResponseDto> getByTripId(Long tripId);
}
