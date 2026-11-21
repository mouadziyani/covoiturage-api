package ma.youcode.covoiturageapi.trip.service.inter;

import ma.youcode.covoiturageapi.common.dto.PageResponse;
import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripSearchResultDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TripService {
    TripResponseDto create(TripRequestDto request);

    List<TripResponseDto> getAll();

    TripResponseDto getById(Long id);

    TripResponseDto update(Long id, TripRequestDto request);

    List<TripResponseDto> getByDriverId(Long driverId);

    PageResponse<TripSearchResultDto> search(String departureCity, String arrivalCity, LocalDate date,
                                             Integer seats, BigDecimal maxPrice, int page, int size);

    void complete(Long id, Long driverId);

    void cancel(Long id, Long driverId);

    void delete(Long id);
}
