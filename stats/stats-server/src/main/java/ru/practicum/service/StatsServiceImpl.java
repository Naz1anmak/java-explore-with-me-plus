package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.repository.StatsRepository;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto createHit(CreateEndpointHitDto createEndpointHitDto) {
        return null;
    }

    @Override
    public ViewStatsDto getStats() {
        return null;
    }
}
