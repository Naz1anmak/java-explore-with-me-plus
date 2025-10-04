package ru.practicum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record StatsRequest(
        LocalDateTime start,
        LocalDateTime end,
        List<String> uris,
        boolean unique
) {
    public static StatsRequest of(String start, String end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new StatsRequest(
                LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter),
                uris,
                unique
        );
    }
}
