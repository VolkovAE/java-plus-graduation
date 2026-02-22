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
        return new UserDto(userId, null, null);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Integer> ids) {
        return ids.stream()
                .map(id -> new UserDto(id, null, null))
                .toList();
    }
}
