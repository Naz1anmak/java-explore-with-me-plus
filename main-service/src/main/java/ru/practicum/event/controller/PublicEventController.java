package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SearchEventPublicRequest;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@ModelAttribute @Valid SearchEventPublicRequest request,
                                               HttpServletRequest httpRequest
    ) {
        int size = (request.size() != null && request.size() > 0) ? request.size() : 10;
        int from = request.from() != null ? request.from() : 0;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        log.debug("Controller: getEventsPublic filters={}", request);
        return eventService.getEventsPublic(request, pageRequest, httpRequest.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable("id") @Positive Long eventId, HttpServletRequest request) {
        log.debug("Controller: getEventByIdPublic eventId={}", eventId);
        return eventService.getEventByIdPublic(eventId, request.getRemoteAddr());
    }
}
