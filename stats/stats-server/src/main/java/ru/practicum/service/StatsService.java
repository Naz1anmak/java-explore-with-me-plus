package ru.practicum.service;

import jakarta.validation.Valid;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.StatsRequest;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void createHit(@Valid CreateEndpointHitDto createEndpointHitDto);

    List<ViewStatsDto> getStats(StatsRequest request);
}
