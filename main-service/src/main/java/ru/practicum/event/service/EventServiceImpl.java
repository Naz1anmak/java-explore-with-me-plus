package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.CreateEndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.*;
import ru.practicum.event.repository.SearchEventSpecifications;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EventErrorException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(Long userId, Pageable pageable) {
        userService.getUserById(userId);
        Page<Event> eventsPage = eventRepository.findAllByInitiatorIdOrderByCreatedOnDesc(userId, pageable);

        if (eventsPage.isEmpty()) return List.of();

        List<Long> eventIds = eventsPage.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);
        Map<Long, Long> views = getViewsForEvents(eventIds);

        return eventsPage.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        validateDateEvent(newEventDto.eventDate(), 2);

        User user = userService.getUserById(userId);
        Event event = eventMapper.toEvent(newEventDto);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING.toString());

        return eventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId, String ip) {
        userService.checkUser(userId);
        Event event = getEventOrThrow(eventId, userId);

        saveHit( "/events/" + eventId, ip);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event.getId()));
        Map<Long, Long> views = getViewsForEvents(List.of(event.getId()));

        return eventMapper.toEventFullDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L)
        );
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        userService.checkUser(userId);
        Event event = getEventOrThrow(eventId, userId);

        validateEventStateForUpdate(event);
        validateDateEvent(request.eventDate(), 2);
        validateParticipantLimit(request.participantLimit());

        eventMapper.updateEventFromUserRequest(request, event);
        updateEventState(event, request);

        Event updatedEvent = eventRepository.save(event);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(updatedEvent.getId()));
        Map<Long, Long> views = getViewsForEvents(List.of(updatedEvent.getId()));

        return eventMapper.toEventFullDto(
                updatedEvent,
                confirmedRequests.getOrDefault(updatedEvent.getId(), 0L),
                views.getOrDefault(updatedEvent.getId(), 0L)
        );
    }

    @Override
    public Event getEventOrThrow(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Событие c id " + eventId + " не найдено"));
    }

    @Override
    public List<EventFullDto> getEventsAdmin(SearchEventAdminRequest request, Pageable pageable) {
        validateRangeStartAndEnd(request.rangeStart(), request.rangeEnd());

        Specification<Event> specification = SearchEventSpecifications.addWhereNull();
        if (request.users() != null && !request.users().isEmpty())
            specification = specification.and(SearchEventSpecifications.addWhereUsers(request.users()));
        if (request.states() != null && !request.states().isEmpty())
            specification = specification.and(SearchEventSpecifications.addWhereStates(request.states()));
        if (request.categories() != null && !request.categories().isEmpty())
            specification = specification.and(SearchEventSpecifications.addWhereCategories(request.categories()));
        if (request.rangeStart() != null)
            specification = specification.and(SearchEventSpecifications.addWhereStartsBefore(request.rangeStart()));
        if (request.rangeEnd() != null)
            specification = specification.and(SearchEventSpecifications.addWhereEndsAfter(request.rangeEnd()));
        if (request.rangeStart() == null && request.rangeEnd() == null)
            specification = specification.and(SearchEventSpecifications.addWhereStartsBefore(LocalDateTime.now()));

        Page<Long> eventIdsPage = eventRepository.findAll(specification, pageable).map(Event::getId);
        List<Long> eventIds = eventIdsPage.getContent();

        if (eventIds.isEmpty()) return List.of();
        List<Event> events = eventRepository.findAllByEventIds(eventIds);

        if (events.isEmpty()) return List.of();
        List<Long> searchEventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(searchEventIds);
        Map<Long, Long> views = getViewsForEvents(searchEventIds);

        return events.stream()
                .map(event -> eventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findByIdNew(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие c id " + eventId + " не найдено"));

        if (request.stateAction() != null) {
            StateActionAdmin stateActionAdmin = request.stateAction();
            EventState currentState = EventState.valueOf(event.getState());

            if (stateActionAdmin == StateActionAdmin.PUBLISH_EVENT) {
                if (currentState != EventState.PENDING)
                    throw new EventErrorException("Событие можно публиковать пока оно не опубликовано");
                validateDateEvent(event.getEventDate(), 1);
                event.setState(EventState.PUBLISHED.toString());
                event.setPublishedOn(LocalDateTime.now());
            }
            if (stateActionAdmin == StateActionAdmin.REJECT_EVENT) {
                if (currentState == EventState.PUBLISHED)
                    throw new EventErrorException("Событие можно отклонить пока оно не опубликовано");
                event.setState(EventState.CANCELED.toString());
            }
        }
        eventMapper.updateEventFromAdminRequest(request, event);
        Event updatedEvent = eventRepository.save(event);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(updatedEvent.getId()));
        Map<Long, Long> views = getViewsForEvents(List.of(updatedEvent.getId()));

        return eventMapper.toEventFullDto(
                updatedEvent,
                confirmedRequests.getOrDefault(updatedEvent.getId(), 0L),
                views.getOrDefault(updatedEvent.getId(), 0L)
        );
    }

    @Override
    public List<EventShortDto> getEventsPublic(SearchEventPublicRequest request, Pageable pageable, String ip) {
        validateRangeStartAndEnd(request.rangeStart(), request.rangeEnd());

        Specification<Event> specification = SearchEventSpecifications.addWhereNull();
        if (request.text() != null && !request.text().trim().isEmpty())
            specification = specification.and(SearchEventSpecifications.addLikeText(request.text()));
        if (request.categories() != null && !request.categories().isEmpty())
            specification = specification.and(SearchEventSpecifications.addWhereCategories(request.categories()));
        if (request.paid() != null)
            specification = specification.and(SearchEventSpecifications.isPaid(request.paid()));
        LocalDateTime rangeStart = (request.rangeStart() == null && request.rangeEnd() == null) ?
                LocalDateTime.now() : request.rangeStart();
        if (rangeStart != null)
            specification = specification.and(SearchEventSpecifications.addWhereStartsBefore(rangeStart));
        if (request.rangeEnd() != null)
            specification = specification.and(SearchEventSpecifications.addWhereEndsAfter(request.rangeEnd()));
        if (Boolean.TRUE.equals(request.onlyAvailable()))
            specification = specification.and(SearchEventSpecifications.addWhereAvailableSlots());

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        if (events.isEmpty()) return List.of();

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);
        Map<Long, Long> views = getViewsForEvents(eventIds);

        List<EventShortDto> result = events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());

        saveHit("/events", ip);

        if ("VIEWS".equals(request.sort())) {
            return result.stream()
                    .sorted(Comparator.comparing(EventShortDto::views).reversed())
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, String ip) {
        Event event = eventRepository.findByIdNew(eventId)
                .filter(ev -> EventState.PUBLISHED.toString().equals(ev.getState()))
                .orElseThrow(() -> new EventNotFoundException("Событие c id " + eventId + " не найдено"));

        saveHit("/events/" + eventId, ip);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event.getId()));
        Map<Long, Long> views = getViewsForEvents(List.of(event.getId()));

        return eventMapper.toEventFullDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(event.getId(), 0L)
        );
    }

    private void validateRangeStartAndEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd))
            throw new EventErrorException("Дата начала не может быть позже даты окончания");
    }

    private void updateEventState(Event event, UpdateEventUserRequest request) {
        if (request.stateAction() != null) {
            if (request.stateAction() == StateActionUser.SEND_TO_REVIEW)
                event.setState(EventState.PENDING.toString());
            if (request.stateAction() == StateActionUser.CANCEL_REVIEW)
                event.setState(EventState.CANCELED.toString());
        }
    }

    private void validateParticipantLimit(Integer limitParticipant) {
        if (limitParticipant != null && limitParticipant < 0)
            throw new EventErrorException("Лимит участников не может быть отрицательным");
    }

    private void validateEventStateForUpdate(Event event) {
        if (EventState.valueOf(event.getState()) == EventState.PUBLISHED)
            throw new EventErrorException("Изменять можно только не опубликованные события");
    }

    private void saveHit(String path, String ip) {
        CreateEndpointHitDto endpointHitDto = new CreateEndpointHitDto("main-service", path, ip, LocalDateTime.now());
        statsClient.saveHit(endpointHitDto);
    }

    private void validateDateEvent(LocalDateTime eventDate, long minHoursBeforeStartEvent) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(minHoursBeforeStartEvent)))
            throw new EventErrorException("Дата начала события не может быть ранее чем через " + minHoursBeforeStartEvent + " часа(ов)");
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Map.of();

        List<EventWithCountConfirmedRequests> events = requestRepository.findCountConfirmedRequestsByEventIds(eventIds);
        Map<Long, Long> confirmedRequests = eventIds.stream()
                .collect(Collectors.toMap(id -> id, id -> 0L));
        events.forEach(dto -> confirmedRequests.put(dto.getEventId(), dto.getCountConfirmedRequests()));

        return confirmedRequests;
    }

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Map.of();

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        LocalDateTime start = eventRepository.findFirstByOrderByCreatedOnAsc().getCreatedOn();
        LocalDateTime end = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

        Map<Long, Long> views = eventIds.stream()
                .collect(Collectors.toMap(id -> id, id -> 0L));

        if (stats != null && !stats.isEmpty()) {
            stats.forEach(stat -> {
                Long eventId = getEventIdFromUri(stat.uri());
                if (eventId > -1L) {
                    views.put(eventId, stat.hits());
                }
            });
        }
        return views;
    }

    private Long getEventIdFromUri(String uri) {
        try {
            return Long.parseLong(uri.substring("/events".length() + 1));
        } catch (Exception e) {
            return -1L;
        }
    }
}
