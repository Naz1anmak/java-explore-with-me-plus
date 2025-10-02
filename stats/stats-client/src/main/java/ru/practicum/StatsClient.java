package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    final RestClient restClient;

    public StatsClient(RestClient.Builder restClientBuilder, @Value("${stats.server.url:http://localhost:9090}") String statServerUrl) {
        this.restClient = restClientBuilder
                .baseUrl(statServerUrl)
                .build();
    }

    public void hit(EndpointHitDto endpointHitDto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Ошибка передачи статистики: " + e.getMessage());
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/stats")
                    .queryParam("start", start.format(formatter))
                    .queryParam("end", end.format(formatter))
                    .queryParam("unique", unique);
            if (uris != null && !uris.isEmpty()) {
                uris.forEach(uri -> uriBuilder.queryParam("uris", uri));
            }
            return restClient.get()
                    .uri(uriBuilder.build().toUriString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            System.err.println("Ошибка передачи статистики: " + e.getMessage());
            return List.of();
        }
    }

}