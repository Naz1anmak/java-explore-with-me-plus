package ru.practicum.event.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventWithCountConfirmedRequests {
    private Long eventId;
    private Long countConfirmedRequests;
}
