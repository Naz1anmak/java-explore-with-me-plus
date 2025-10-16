package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.model.User;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", ignore = true) //TODO убрать после реализации Category
    Event fromNewEvent(NewEventDto dto, User initiator, Category category, EventState state);

    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);

    EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "initiator", ignore = true)
//    @Mapping(target = "createdOn", ignore = true)
//    @Mapping(target = "publishedOn", ignore = true)
//    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEventFromUserRequest(UpdateEventUserRequest request, @MappingTarget Event event);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "initiator", ignore = true)
//    @Mapping(target = "createdOn", ignore = true)
//    @Mapping(target = "publishedOn", ignore = true)
//    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEventFromAdminRequest(UpdateEventAdminRequest request, @MappingTarget Event event);
}
