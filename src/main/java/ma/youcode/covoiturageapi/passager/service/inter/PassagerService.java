package ma.youcode.covoiturageapi.passager.service.inter;

import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
import ma.youcode.covoiturageapi.passager.dto.response.PassagerResponseDto;

import java.util.List;

public interface PassagerService {
    PassagerResponseDto create(PassagerRequestDto request);

    List<PassagerResponseDto> findAll();

    PassagerResponseDto findById(Long id);

    PassagerResponseDto findByEmail(String email);

    PassagerResponseDto update(Long id, PassagerRequestDto request);
}
