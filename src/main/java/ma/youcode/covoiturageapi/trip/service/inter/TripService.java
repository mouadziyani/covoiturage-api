package ma.youcode.covoiturageapi.trip.service.inter;

import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    TripResponseDto create(TripRequestDto request);

    List<TripResponseDto> getAll();

    TripResponseDto getById(Long id);

    TripResponseDto update(Long id, TripRequestDto request);

    List<TripResponseDto> getByDriverId(Long driverId);

    List<TripResponseDto> search(String departureCity, String arrivalCity, LocalDate date);

    void delete(Long id);
}
