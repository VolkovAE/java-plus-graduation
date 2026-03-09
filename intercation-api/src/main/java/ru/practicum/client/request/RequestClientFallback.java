package ru.practicum.client.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.request.RequestStatus;
import ru.practicum.storage.ConfirmedCount;

import java.util.List;

@Component
@Slf4j
public class RequestClientFallback implements RequestClient {
    @Override
    public Integer countConfirmedByEventId(Integer eventId) {
        log.error("Сервис request-service не доступен. Метод countConfirmedByEventId() возвращает 0.");

        return 0;
    }

    @Override
    public List<ConfirmedCount> countConfirmedForEventIds(List<Integer> eventIds) {
        log.error("Сервис request-service не доступен. Метод countConfirmedForEventIds() возвращает пустой список.");

        return List.of();
    }

    @Override
    public Integer countByEventIdAndStatus(Integer eventId, RequestStatus requestStatus) {
        log.error("Сервис request-service не доступен. Метод countByEventIdAndStatus() возвращает 0.");

        return 0;
    }

    @Override
    public List<ParticipationRequestDto> findByEventId(Integer eventId) {
        log.error("Сервис request-service не доступен. Метод findByEventId() возвращает пустой список.");

        return List.of();
    }

    @Override
    public List<ParticipationRequestDto> findAllByIdIn(List<Integer> requestIds) {
        log.error("Сервис request-service не доступен. Метод findAllByIdIn() возвращает пустой список.");

        return List.of();
    }

    @Override
    public ParticipationRequestDto update(ParticipationRequestDto participationRequestDto) {
        log.error("Сервис request-service не доступен. Метод update сделал обновление записи в БД.");

        return null;
    }

    @Override
    public Boolean checkUserParticipation(Integer userId, Integer eventId) {
        log.error("Сервис request-service не доступен.");

        return null;
    }
}
