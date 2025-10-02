package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(EndpointHitDto endpointHitDto);

    EndpointHitDto toDto(EndpointHit endpointHit);
}
