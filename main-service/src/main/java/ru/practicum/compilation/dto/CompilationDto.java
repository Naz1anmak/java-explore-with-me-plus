package ru.practicum.compilation.dto;

import ru.practicum.event.dto.EventShortDto;

import java.util.List;

public record CompilationDto(
        Long id,
        List<EventShortDto> events,
        boolean pinned,
        String title
) {
}
