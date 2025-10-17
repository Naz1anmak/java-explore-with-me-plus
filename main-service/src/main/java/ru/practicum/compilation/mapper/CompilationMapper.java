package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toEntity(NewCompilationDto request);

    @Mapping(target = "events", source = "events", qualifiedByName = "eventsToEventShortDos")
    CompilationDto toDto(Compilation compilation);

    @Named("eventsToEventShortDos")
    default Set<EventShortDto> eventsToEventShortDos(Set<Event> events) {
        if (events == null) return null;
        return events.stream()
                .map(EventMapper.eventMapper::toEventShortWithoutRequestsAndViewsDto)
                .collect(Collectors.toSet());
    }
}
