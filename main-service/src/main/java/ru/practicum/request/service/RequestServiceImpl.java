package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventWithCountConfirmedRequests;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventService eventService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        Event event = eventService.getEventOrThrow(eventId, userId);

        if (!event.getInitiator().getId().equals(userId))
            throw new ValidationException("Пользователь с id=" + userId + " не является создателем события");

        log.info("Получение информации о запросах на участие в событии с id={}", eventId);
        return requestRepository.findAllParticipationRequestByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventService.getEventOrThrow(eventId, userId);

        if (!event.getInitiator().getId().equals(userId))
            throw new ValidationException("Пользователь с id=" + userId + " не является создателем события");

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0)
            throw new ValidationException("Для данного события не нужна модерация заявок");

        RequestStatus newStatus = request.status();

        if (newStatus == RequestStatus.PENDING)
            throw new ValidationException("Устанавливать можно только статусы CONFIRMED и REJECTED");

        List<Request> requestsForUpdate = requestRepository.findAllRequestById(request.requestIds());

        if (requestsForUpdate.size() != request.requestIds().size())
            throw new NotFoundException("Не все запросы найдены");

        Map<Long, Long> confirmedRequestsMap = new HashMap<>();
        if (eventId != null) {
            EventWithCountConfirmedRequests result = requestRepository.findConfirmedRequestsCountByEventIds(eventId);

            if (result != null && Objects.equals(result.getEventId(), eventId)) {
                confirmedRequestsMap.put(eventId, result.getCountConfirmedRequests());
            } else {
                confirmedRequestsMap.put(eventId, 0L);
            }
        }
        Long currentCountConfirmedRequests = confirmedRequestsMap.getOrDefault(eventId, 0L);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request requestList : requestsForUpdate) {
            if (requestList.getStatus() != RequestStatus.PENDING)
                throw new ValidationException("Можно изменять только запросы в статусе PENDING");

            if (!requestList.getEvent().getId().equals(eventId))
                throw new ConflictException("Запрос с id = " + requestList.getId() +
                                                " не относится к событию с id = " + eventId);

            if (newStatus == RequestStatus.CONFIRMED) {
                int participantLimit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
                if (participantLimit > 0 && currentCountConfirmedRequests >= participantLimit)
                    throw new ConflictException("Свободных мест больше нет");
                currentCountConfirmedRequests++;

                requestList.confirmed();
                confirmedRequests.add(requestMapper.toDto(requestList));
            } else {
                requestList.rejected();
                rejectedRequests.add(requestMapper.toDto(requestList));
            }
        }

        requestRepository.saveAll(requestsForUpdate);

        log.info("Обновление статусов заявок на участие в событии с id={}", eventId);
        return requestMapper.toEventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
    }
}
