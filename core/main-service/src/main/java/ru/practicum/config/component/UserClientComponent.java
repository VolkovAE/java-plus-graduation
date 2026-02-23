package ru.practicum.config.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.client.user.UserClient;
import ru.practicum.dto.user.UserDto;
import ru.practicum.events.model.Event;
import ru.practicum.exception.ServiceUnavailableException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.util.StringConstants.ERROR_MESSAGE_USER_SERVICE_UNAVAILABLE;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientComponent {
    private final UserClient userRepository;

    public UserDto getUserById(Integer userId) {
        UserDto userDto = userRepository.getUserById(userId);

        if (userDto.getEmail() == null && userDto.getName() == null) throwUserServiceUnavailable();

        return userDto;
    }

    public List<UserDto> getUsersByIds(List<Integer> ids) {
        List<UserDto> userDtoList = userRepository.getUsersByIds(ids);

        if (userDtoList.isEmpty())
            return userDtoList;  // пустой список возвращаем сразу, то есть либо нет ничего, либо параметр ids пуст

        if (userDtoList.getFirst().getEmail() == null && userDtoList.getFirst().getName() == null)
            throwUserServiceUnavailable();

        return userDtoList;
    }

    public void throwUserServiceUnavailable() {
        log.warn(ERROR_MESSAGE_USER_SERVICE_UNAVAILABLE);

        throw new ServiceUnavailableException(ERROR_MESSAGE_USER_SERVICE_UNAVAILABLE);
    }

    public Map<Integer, UserDto> getUsersByIdsMap(List<Event> events) {
        List<Integer> idsUsers = events.stream().map(Event::getInitiatorId).toList();
        List<UserDto> userDtoList = getUsersByIds(idsUsers);

        return userDtoList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }
}
