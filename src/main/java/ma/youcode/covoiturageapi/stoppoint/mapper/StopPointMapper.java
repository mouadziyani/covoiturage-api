package ma.youcode.covoiturageapi.stoppoint.mapper;

import ma.youcode.covoiturageapi.stoppoint.dto.response.StopPointResponseDto;
import ma.youcode.covoiturageapi.stoppoint.dto.request.StopPointRequestDto;
import ma.youcode.covoiturageapi.stoppoint.entity.StopPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StopPointMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "city", expression = "java(request.city().trim())")
    StopPoint toEntity(StopPointRequestDto request);

    @Mapping(target = "tripId", source = "trip.id")
    StopPointResponseDto toResponse(StopPoint stopPoint);
}
