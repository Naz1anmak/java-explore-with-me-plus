package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.event.model.StateActionUser;

import java.time.LocalDateTime;

public record UpdateEventUserRequest(
        @Size(min = 20, max = 2000, message = "Недопустимое количество символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Описание должно содержать от {min} до {max} символов")
        String description,

        @Future(message = "Дата события должна быть в будущем")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        LocationDto location,

        Boolean paid,

        @PositiveOrZero(message = "Лимит участников события должен быть нулевым или больше нуля")
        Integer participantLimit,

        Boolean requestModeration,

        StateActionUser stateAction,

        @Size(min = 3, max = 120, message = "Заголовок должен содержать от {min} до {max} символов")
        String title
) {
}
