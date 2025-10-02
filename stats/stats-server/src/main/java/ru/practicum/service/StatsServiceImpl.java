package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Override
    public EndpointHitDto createHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEntity(endpointHitDto);
        System.out.println(endpointHit);
        EndpointHit savedEndpointHit = statsRepository.save(endpointHit);
        return endpointHitMapper.toDto(savedEndpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        System.out.println(start);
        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findUniqueStats(start, end, uris);
        } else {
            return statsRepository.findNotUniqueStats(start, end, uris);
        }
    }

}
