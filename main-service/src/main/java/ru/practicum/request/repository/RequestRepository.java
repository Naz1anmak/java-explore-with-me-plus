package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.EventWithCountConfirmedRequests;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            SELECT NEW ru.practicum.event.model.EventWithCountConfirmedRequests(r.event.id, COUNT(r))
            FROM Request r
            WHERE r.event.id IN :eventIds AND r.status = ru.practicum.request.model.RequestStatus.CONFIRMED
            GROUP BY r.event.id"""
    )
    List<EventWithCountConfirmedRequests> findCountConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);

    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.event
            LEFT JOIN FETCH r.requester
            WHERE r.event.id = :eventId"""
    )
    List<Request> findAllParticipationRequestByEventId(@Param("eventId") Long eventId);

    @Query("""
            SELECT r FROM Request r
            LEFT JOIN FETCH r.event
            LEFT JOIN FETCH r.requester
            WHERE r.id IN :requestIds"""
    )
    List<Request> findAllRequestById(@Param("requestIds") List<Long> requestIds);

    @Query("""
            SELECT NEW ru.practicum.event.model.EventWithCountConfirmedRequests(r.event.id, COUNT(r))
            FROM Request r
            WHERE r.event.id = :eventId AND r.status = ru.practicum.request.model.RequestStatus.CONFIRMED
            GROUP BY r.event.id"""
    )
    EventWithCountConfirmedRequests findConfirmedRequestsCountByEventIds(@Param("eventId") Long eventId);
}
