package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.exception.StatsClientException;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;

    @Autowired
    public StatClient(@Value("${stats-server.url:http://localhost:9090}") String statsUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            String errorMessage = String.format("Ошибка сервиса статистики: %d %s",
                                    response.getStatusCode().value(), response.getStatusText());
                            log.error(errorMessage);
                            throw new StatsClientException(errorMessage);
                        })
                .build();
    }

    public void hit(EndpointHitDto endpointHitDto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Статистика успешно отправлена: app={}, uri={}, ip={}",
                    endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp());
        } catch (Exception e) {
            log.error("Ошибка при сохранении статистики: {}, {}", endpointHitDto, e.getMessage());
            throw new StatsClientException("Ошибка при отправке статистики", e);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateGetStatsParam(start, end);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        log.info("Запрос статистики: start={}, end={}, uris={}, unique={}",
                startStr, endStr, uris, unique);

        return restClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path("/stats")
                            .queryParam("start", startStr)
                            .queryParam("end", endStr)
                            .queryParam("unique", unique);

                    if (uris != null && !uris.isEmpty()) {
                        for (String uri : uris) {
                            builder = builder.queryParam("uris", uri);
                        }
                    }

                    return builder.build(false);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {});
    }

    private void validateGetStatsParam(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new IllegalArgumentException("Дата начала не может быть нулевой");
        }
        if (end == null) {
            throw new IllegalArgumentException("Дата окончания не может быть нулевой");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }
    }
}
