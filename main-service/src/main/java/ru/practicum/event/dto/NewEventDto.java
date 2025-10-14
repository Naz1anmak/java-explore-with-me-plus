package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record NewEventDto(
        @NotBlank(message = "Краткое описание не может быть пустым")
        @Size(min = 20, max = 2000, message = "Аннотация должна содержать от 20 до 2000 символов")
        String annotation,

        @NotNull(message = "Id категории не может быть null")
        Long category,

        @NotBlank(message = "Описание события не может быть пустым")
        @Size(min = 20, max = 7000, message = "Описание должно содержать от 20 до 7000 символов")
        String description,

        @NotNull(message = "Дата события не может быть null")
        @Future(message = "Дата события должна быть в будущем")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        @NotNull(message = "Место проведения события не может быть null")
        LocationDto location,

        boolean paid,

        @PositiveOrZero(message = "Лимит участников события должен быть нулевым или больше нуля")
        Integer participantLimit,

        boolean requestModeration,

        @NotBlank(message = "Название события не может быть пустым")
        @Size(min = 3, max = 120, message = "Заголовок должен содержать от 3 до 120 символов")
        String title
) {
}
