package ma.youcode.covoiturageapi.car.mappper;

import ma.youcode.covoiturageapi.car.dto.request.CarRequestDto;
import ma.youcode.covoiturageapi.car.dto.response.CarResponseDto;
import ma.youcode.covoiturageapi.car.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "size", source = "size")
    @Mapping(target = "driverId", source = "driver.id")
    CarResponseDto toResponse(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "size", source = "size")
    @Mapping(target = "driver", ignore = true)
    Car toEntity(CarRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "size", source = "size")
    @Mapping(target = "driver", ignore = true)
    Car updateEntity(CarRequestDto dto , @MappingTarget Car car);
}
