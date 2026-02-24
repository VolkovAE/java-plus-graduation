package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import static ru.practicum.util.Constants.FORMATTER;

@Component
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        if (request == null) {
            return null;
        }

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated().format(FORMATTER)); // Форматируем дату в строку
        dto.setEvent(request.getEventId());
        dto.setRequester(request.getRequesterId());
        dto.setStatus(request.getStatus().name()); // Получаем имя статуса из enum

        return dto;
    }
}
