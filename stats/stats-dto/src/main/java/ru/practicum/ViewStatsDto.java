package ru.practicum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ViewStatsDto(
        @NotBlank(message = "Название приложения не может быть пустым")
        String app,

        @NotBlank(message = "URI не может быть пустым")
        String uri,

        @NotNull(message = "Количество hits не может быть null")
        Long hits
) {
}