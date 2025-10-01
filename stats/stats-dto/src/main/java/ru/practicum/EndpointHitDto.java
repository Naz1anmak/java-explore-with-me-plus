package ru.practicum;

import java.time.LocalDateTime;

public record EndpointHitDto(
        String app,
        String uri,
        String ip,
        LocalDateTime timestamp
) {
}
