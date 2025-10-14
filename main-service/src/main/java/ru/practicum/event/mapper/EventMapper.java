package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", ignore = true)
    Event toEvent(NewEventDto newEventDto);

    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);

    EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views);

    @Mapping(target = "category", ignore = true)
    void updateEventFromUserRequest(UpdateEventUserRequest request, @MappingTarget Event event);

    @Mapping(target = "category", ignore = true)
    void updateEventFromAdminRequest(UpdateEventAdminRequest request, @MappingTarget Event event);
}
