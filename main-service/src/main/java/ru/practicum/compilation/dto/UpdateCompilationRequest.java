package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateCompilationRequest(
        Set<Long> events,

        boolean pinned,

        @Size(min = 1, max = 50, message = "Заголовок должен содержать от 1 до 50 символов")
        String title
) {
}
