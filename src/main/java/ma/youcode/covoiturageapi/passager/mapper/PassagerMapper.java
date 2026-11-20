    package ma.youcode.covoiturageapi.passager.mapper;

    import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
    import ma.youcode.covoiturageapi.passager.dto.response.PassagerResponseDto;
    import ma.youcode.covoiturageapi.passager.entity.Passager;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;
    import org.mapstruct.MappingTarget;

    @Mapper(componentModel = "spring")
    public interface PassagerMapper {

        PassagerResponseDto toResponse(Passager passager);

        @Mapping(target = "id", ignore = true)
        Passager toEntity(PassagerRequestDto dto);

        @Mapping(target = "id",ignore = true)
        Passager updateEntity(PassagerRequestDto dto , @MappingTarget Passager passager);
    }
