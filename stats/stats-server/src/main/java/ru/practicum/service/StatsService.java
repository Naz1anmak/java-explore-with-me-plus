package ru.practicum.service;

import jakarta.validation.Valid;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

public interface StatsService {
    EndpointHitDto createHit(@Valid CreateEndpointHitDto createEndpointHitDto);

    ViewStatsDto getStats();
}
