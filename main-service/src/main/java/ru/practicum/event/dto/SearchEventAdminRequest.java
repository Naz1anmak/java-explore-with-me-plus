package ru.practicum.event.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public record SearchEventAdminRequest(
        List<Long> users,

        List<EventState> states,

        List<Long> categories,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rangeStart,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rangeEnd,

        Integer from,

        Integer size
) {
}