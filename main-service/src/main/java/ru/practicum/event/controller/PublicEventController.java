package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SearchEventPublicRequest;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsPublic(
            @ModelAttribute @Valid SearchEventPublicRequest request,
            HttpServletRequest httpRequest
    ) {
        int size = (request.size() != null && request.size() > 0) ? request.size() : 10;
        int from = request.from() != null ? request.from() : 0;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<EventShortDto> events = eventService.getEventsPublic(request, pageRequest, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(events);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdPublic(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto event = eventService.getEventByIdPublic(id, request.getRemoteAddr());
        return ResponseEntity.ok(event);
    }
}
