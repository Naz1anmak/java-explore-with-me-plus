package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EndpointHit;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
}
