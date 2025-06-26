package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.client.exception.StatisticClientException;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.dto.output.GetStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String statsUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            throw new StatisticClientException("Ошибка сервиса статистики: " + response.getStatusText());
                        })
                .build();
    }

    public void hit(StatisticDto statisticDto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .body(statisticDto)
                    .retrieve()
                    .toEntity(GetStatisticDto.class);
            log.info("Запрос на сохранение статистики в клиенте");
        } catch (Exception e) {
            log.error("Ошибка при сохранении статистики в клиенте: {}, {}", statisticDto, e.getMessage());
            throw new StatisticClientException("Ошибка при отправке статистики", e);
        }
    }

    public List<GetStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            log.info("Запрос на получение статистики в клиенте");
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/stats")
                            .queryParam("start", start.format(dateTimeFormat))
                            .queryParam("end", end.format(dateTimeFormat))
                            .queryParam("uris", uris != null ? uris : Collections.emptyList())
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Ошибка при получении статистики в клиенте параметры, {}", e.getMessage());
            throw new StatisticClientException("Ошибка при получении статистики", e);
        }
    }
}