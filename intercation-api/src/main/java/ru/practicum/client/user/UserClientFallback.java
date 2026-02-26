package ru.practicum.client.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@Component
@Slf4j
public class UserClientFallback implements UserClient {
    @Override
    public UserDto getUserById(Integer userId) {
        log.error("Сервис user-service не доступен. Метод getUserById возвращает fallback-значение.");

        return new UserDto(userId, null, null);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Integer> ids) {
        log.error("Сервис user-service не доступен. Метод getUsersByIds возвращает fallback-значение.");

        return ids.stream()
                .map(id -> new UserDto(id, null, null))
                .toList();
    }
}
