package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.SearchEventAdminRequest;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventFullDto> getEventsAdmin(@ModelAttribute @Valid SearchEventAdminRequest request) {
        int size = (request.size() != null && request.size() > 0) ? request.size() : 10;
        int from = request.from() != null ? request.from() : 0;
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventService.getEventsAdmin(request, pageRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest request
    ) {
        return eventService.updateEventAdmin(eventId, request);
    }

}
