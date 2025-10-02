package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {
    private final RestClient restClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void saveHit(CreateEndpointHitDto createEndpointHitDto) {
        restClient.post()
                .uri("/hit")
                .body(createEndpointHitDto)
                .retrieve()
                .toBodilessEntity();

        log.debug("Запись обращения к эндпоинту успешно сохранена: приложение {}, URI {}",
                createEndpointHitDto.app(), createEndpointHitDto.uri());
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        ViewStatsDto[] stats = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", startStr)
                            .queryParam("end", endStr);
                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    }
                    if (unique != null) {
                        uriBuilder.queryParam("unique", unique);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(ViewStatsDto[].class);

        List<ViewStatsDto> result = stats != null ? List.of(stats) : List.of();
        log.debug("Получено записей статистики  {}", result.size());
        return result;
    }
}