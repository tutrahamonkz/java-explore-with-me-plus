package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import ru.practicum.dto.HitDto;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatsDto;


import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum", "ru.practicum.client"})
public class MainApplication {
    /*public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }*/
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        StatClient stateClient = context.getBean(StatClient.class);
        ResponseEntity<Void> hitResponse = stateClient.hit(HitDto.builder()
                .app("main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.now())
                .build());
        if (hitResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("Запрос hit выполнен успешно");
        } else {
            System.out.println("Ошибка при выполнении запроса hit: " + hitResponse.getStatusCode());
        }
        ResponseEntity<List<StatsDto>> statsResponse = stateClient.getStats("2020-05-05 00:00:00", "2035-05-05 00:00:00", List.of("/events", "/events/1"), false);
        if (statsResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("Запрос getStats выполнен успешно");
            List<StatsDto> stats = statsResponse.getBody();
            stats.forEach(System.out::println);
        } else {
            System.out.println("Ошибка при выполнении запроса getStats: " + statsResponse.getStatusCode());
        }
    }
}

