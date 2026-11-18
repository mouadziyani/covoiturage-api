package ma.youcode.covoiturageapi.driver.service.inter;

import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;

import java.util.List;

public interface DriverService {
    DriverResponseDto create(DriverRequestDto request);

    List<DriverResponseDto> findAll();

    DriverResponseDto findById(Long id);

    List<DriverResponseDto> search(String keyword);

    DriverResponseDto update(Long id, DriverRequestDto request);
}
