package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.practicum.ViewStatsDto(eph.app, eph.uri, COUNT(DISTINCT eph.ip))
            FROM EndpointHit eph
            WHERE eph.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR eph.uri in :uris)
            GROUP BY eph.app, eph.uri
            ORDER BY COUNT(DISTINCT eph.ip) DESC
            """)
    List<ViewStatsDto> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            SELECT new ru.practicum.ViewStatsDto(eph.app, eph.uri, COUNT(eph.id))
            FROM EndpointHit eph
            WHERE eph.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR eph.uri in :uris)
            GROUP BY eph.app, eph.uri
            ORDER BY COUNT(eph.id) DESC
            """)
    List<ViewStatsDto> findNotUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

}
