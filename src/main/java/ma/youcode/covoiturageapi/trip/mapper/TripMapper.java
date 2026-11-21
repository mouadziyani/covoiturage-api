package ma.youcode.covoiturageapi.trip.mapper;

import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.stoppoint.mapper.StopPointMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = StopPointMapper.class)
public interface TripMapper {

    @Mapping(target = "carId", source = "trip.car.id")
    @Mapping(target = "driverId", source = "trip.driver.id")
    @Mapping(
            target = "remainingSeats",
            expression = "java(trip.getProposedSeats() - reservedSeats.intValue())"
    )
    TripResponseDto toResponse(Trip trip, Long reservedSeats);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "stopPoints", ignore = true)
    @Mapping(target = "departureCity", ignore = true)
    @Mapping(target = "arrivalCity", ignore = true)
    @Mapping(target = "departureDateTime", ignore = true)
    Trip toEntity(TripRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "stopPoints", ignore = true)
    @Mapping(target = "departureCity", ignore = true)
    @Mapping(target = "arrivalCity", ignore = true)
    @Mapping(target = "departureDateTime", ignore = true)
    Trip updateEntity(TripRequestDto dto, @MappingTarget Trip trip);
}
