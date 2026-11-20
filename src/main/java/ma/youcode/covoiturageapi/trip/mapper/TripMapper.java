package ma.youcode.covoiturageapi.trip.mapper;

import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "carId", source = "car.id")
    TripResponseDto toResponse(Trip trip);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "status", ignore = true)
    Trip toEntity(TripRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "status", ignore = true)
    Trip updateEntity(TripRequestDto dto , @MappingTarget Trip trip);
}
