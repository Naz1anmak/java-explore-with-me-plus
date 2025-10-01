package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto createHit(@Valid @RequestBody CreateEndpointHitDto createEndpointHitDto) {
        return statsService.createHit(createEndpointHitDto);
    }

    @GetMapping("/stats")
    public ViewStatsDto getStats(@RequestParam("start") String start, @RequestParam("end") String end,
                                 @RequestParam(value = "uris", required = false) List<String> uris,
                                 @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statsService.getStats();
    }
}
