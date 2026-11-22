package ma.youcode.covoiturageapi.reservation.mapper;

import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;
import ma.youcode.covoiturageapi.reservation.entity.Reservation;
import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
import ma.youcode.covoiturageapi.passager.entity.Passager;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "passager", source = "passager")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "boardingStopPoint", source = "boarding")
    @Mapping(target = "alightingStopPoint", source = "destination")
    @Mapping(target = "numberOfSeats", source = "request.numberOfSeats")
    @Mapping(target = "totalAmount", expression = "java(totalAmount.setScale(2, RoundingMode.HALF_UP))")
    Reservation toEntity(ReservationRequestDto request, Passager passager, Trip trip,
                         StopPoint boarding, StopPoint destination, java.math.BigDecimal totalAmount);

    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "boardingStopPointId", source = "boardingStopPoint.id")
    @Mapping(target = "boardingCity", source = "boardingStopPoint.city")
    @Mapping(target = "alightingStopPointId", source = "alightingStopPoint.id")
    @Mapping(target = "alightingCity", source = "alightingStopPoint.city")
    ReservationResponseDto toResponse(Reservation reservation);
}
