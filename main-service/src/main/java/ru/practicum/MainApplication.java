package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@ComponentScan
public class MainApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);

        StatsClient statsClient = context.getBean(StatsClient.class);

        statsClient.hit(new EndpointHitDto("ewm-main-service", "/events/2", "192.163.0.1", LocalDateTime.now()));

        List<String> uris = Arrays.asList("/events/2");
        List<ViewStatsDto> viewStatsDto = statsClient.getStats(LocalDateTime.now().minusYears(10), LocalDateTime.now(), uris, false);

        System.out.println(viewStatsDto);

    }
}
