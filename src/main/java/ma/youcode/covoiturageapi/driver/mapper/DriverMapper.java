package ma.youcode.covoiturageapi.driver.mapper;

import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;
import ma.youcode.covoiturageapi.driver.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
<<<<<<< Updated upstream

@Mapper(componentModel = "spring")
public interface DriverMapper {
    DriverResponseDto toResponse(Driver driver);

    @Mapping(target = "id" , ignore = true)
    Driver toEntity(DriverRequestDto dto);
=======
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    DriverResponseDto toResponse(Driver driver);

    @Mapping(target = "id",ignore = true)
    Driver toEntity(DriverRequestDto driverRequestDto);

    @Mapping(target = "id",ignore = true)
    Driver updateEntity(DriverRequestDto dto , @MappingTarget Driver driver);

>>>>>>> Stashed changes
}
