package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHitDto toDto(EndpointHit endpointHit);

    @Mapping(target = "id", ignore = true)
    EndpointHit fromNewRequest(CreateEndpointHitDto request);
}
