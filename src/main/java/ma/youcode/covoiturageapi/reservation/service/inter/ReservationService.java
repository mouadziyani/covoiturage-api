package ma.youcode.covoiturageapi.reservation.service.inter;

import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;

import java.util.List;

public interface ReservationService {
    ReservationResponseDto create(ReservationRequestDto request);

    List<ReservationResponseDto> getByPassagerId(Long passagerId);

    List<ReservationResponseDto> getByDriverId(Long driverId);

    void cancel(Long reservationId, Long passagerId);
}
