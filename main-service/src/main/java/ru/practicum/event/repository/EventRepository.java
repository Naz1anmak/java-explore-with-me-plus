package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.event.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiatorIdOrderByCreatedOnDesc(Long initiatorId, Pageable pageable);

    Event findFirstByOrderByCreatedOnAsc();

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("""
            SELECT e FROM Event e
            LEFT JOIN FETCH e.category
            LEFT JOIN FETCH e.initiator
            WHERE e.id IN :eventIds"""
    )
    List<Event> findAllByEventIds(@Param("eventIds") List<Long> eventIds);

    @Query("""
            SELECT e FROM Event e
            LEFT JOIN FETCH e.category
            LEFT JOIN FETCH e.initiator
            WHERE e.id = :eventId"""
    )
    Optional<Event> findByIdNew(@Param("eventId") Long eventId);

}
