package ma.youcode.covoiturageapi.car.service.inter;

import ma.youcode.covoiturageapi.car.dto.request.CarRequestDto;
import ma.youcode.covoiturageapi.car.dto.response.CarResponseDto;

import java.util.List;

public interface CarService {
    CarResponseDto create(CarRequestDto request);

    List<CarResponseDto> getAll();

    CarResponseDto getById(Long id);

    CarResponseDto update(Long id, CarRequestDto request);

    List<CarResponseDto> getByDriverId(Long driverId);

    void delete(Long id);
}
