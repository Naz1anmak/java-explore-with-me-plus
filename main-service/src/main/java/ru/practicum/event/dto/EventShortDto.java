package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(
        Long id,

        String annotation,

        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        UserShortDto initiator,

        boolean paid,

        String title,

        Long views
) {
}
