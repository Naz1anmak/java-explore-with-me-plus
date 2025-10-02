package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEndpointHitDto(
        @NotBlank(message = "Название приложения не может быть пустым")
        String app,

        @NotBlank(message = "URI не может быть пустым")
        String uri,

        @NotBlank(message = "IP адрес не может быть пустым")
        String ip,

        @NotNull(message = "Временная метка не может быть null")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
}
