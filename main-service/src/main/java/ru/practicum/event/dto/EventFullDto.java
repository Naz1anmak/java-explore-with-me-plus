package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.EventState;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventFullDto(
        Long id,

        String annotation,

        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        UserShortDto initiator,

        LocationDto location,

        boolean paid,

        Integer participantLimit,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,

        boolean requestModeration,

        EventState state,

        String title,

        Long views
) {
}
