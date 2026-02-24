package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.exception.StatsClientException;
import ru.practicum.exception.StatsServerUnavailable;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.util.Constants.VALUE_DISCOVERY_SERVICES_STATS_SERVER_ID;

@Component
@Slf4j
public class StatClient {
    private final DiscoveryClient discoveryClient;
    private final String statsServiceId;
    private final RetryTemplate retryTemplate;
    private final RestClient restClient;

    @Autowired
    public StatClient(DiscoveryClient discoveryClient,
                      @Value(VALUE_DISCOVERY_SERVICES_STATS_SERVER_ID) String statsServiceId) {
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;

        this.retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        this.restClient = RestClient.builder()
                //.baseUrl(statsUrl)
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
                    .uri(makeUri("/hit"))   // обращаемся по полученному пути к сервису stats-server
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
                            .path(makeUri("/stats").toString())
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
                .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });
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

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable("Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
                    exception);
        }
    }
}
