package ma.youcode.covoiturageapi.reservation.mapper;

import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;
import ma.youcode.covoiturageapi.reservation.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "passagerId", source = "passager.id")
    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "boardingStopPointId", source = "boardingStopPoint.id")
    @Mapping(target = "boardingCity", source = "boardingStopPoint.city")
    @Mapping(target = "alightingStopPointId", source = "alightingStopPoint.id")
    @Mapping(target = "alightingCity", source = "alightingStopPoint.city")
    ReservationResponseDto toResponse(Reservation reservation);
}
