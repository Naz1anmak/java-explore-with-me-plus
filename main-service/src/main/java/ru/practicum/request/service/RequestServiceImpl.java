package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventWithCountConfirmedRequests;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.RequestErrorException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final EventService eventService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Long userId, Long eventId) {
        Event event = eventService.getEventOrThrow(eventId, userId);

        if (!event.getInitiator().getId().equals(userId))
            throw new RequestErrorException("Пользователь с id=" + userId + " не является создателем события");

        return requestRepository.findAllParticipationRequestByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventService.getEventOrThrow(eventId, userId);

        if (!event.getInitiator().getId().equals(userId))
            throw new RequestErrorException("Пользователь с id=" + userId + " не является создателем события");

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0)
            throw new RequestErrorException("Для данного события не нужна модерация заявок");

        RequestStatus newStatus;

        try {
            newStatus = request.status();
        } catch (IllegalArgumentException e) {
            throw new RequestNotFoundException("Несуществующий статус");
        }

        if (newStatus == RequestStatus.PENDING)
            throw new RequestErrorException("Устанавливать можно только статусы CONFIRMED и REJECTED");

        List<Request> requestsForUpdate = requestRepository.findAllRequestById(request.requestIds());

        if (requestsForUpdate.size() != request.requestIds().size())
            throw new RequestErrorException("Не все запросы найдены");

        Map<Long, Long> confirmedRequestsMap = Map.of();
        if (eventId != null) {
            EventWithCountConfirmedRequests result = requestRepository.findConfirmedRequestsCountByEventIds(eventId);
            if (Objects.equals(result.getEventId(), eventId))
                confirmedRequestsMap.put(eventId, result.getCountConfirmedRequests());
            else confirmedRequestsMap.put(eventId, 0L);
        }
        Long currentCountConfirmedRequests = confirmedRequestsMap.getOrDefault(eventId, 0L);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request requestList : requestsForUpdate) {
            if (requestList.getStatus() != RequestStatus.PENDING)
                throw new RequestErrorException("Можно изменять только запросы в статусе PENDING");

            if (!requestList.getEvent().getId().equals(eventId))
                throw new RequestErrorException("Запрос с id = " + requestList.getId() +
                                                " не относится к событию с id = " + eventId);

            if (newStatus == RequestStatus.CONFIRMED) {
                int participantLimit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
                if (participantLimit > 0 && currentCountConfirmedRequests >= participantLimit)
                    throw new RequestErrorException("Свободных мест больше нет");
                currentCountConfirmedRequests++;

                requestList.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toDto(requestList));
            } else {
                requestList.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(requestList));
            }
        }

        requestRepository.saveAll(requestsForUpdate);

        return requestMapper.toEventRequestStatusUpdateResultDto(confirmedRequests, rejectedRequests);
    }

}
