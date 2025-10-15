package ru.practicum.event.model;

import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class EventWithCountConfirmedRequests {
    private Long eventId;
    private Long countConfirmedRequests;
}
