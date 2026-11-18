    package ma.youcode.covoiturageapi.passager.mapper;

    import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
    import ma.youcode.covoiturageapi.passager.entity.Passager;
    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    @Mapper(componentModel = "spring")
    public interface PassagerMapper {

        PassagerRequestDto toResponse(Passager passager);

        @Mapping(target = "id", ignore = true)
        Passager toEntity(PassagerRequestDto dto);

    }
