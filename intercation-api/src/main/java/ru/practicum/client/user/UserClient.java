package ru.practicum.client.user;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.user.UserDto;

import java.util.List;

import static ru.practicum.util.Constants.*;

@FeignClient(name = NAME_USER_SERVICE, path = PATH_BOX_USERS, fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping(PATH_BOX_USERS_ID)
    UserDto getUserById(@PathVariable @NotNull Integer userId);

    @GetMapping(PATH_BOX_USERS_LIST)
    List<UserDto> getUsersByIds(@RequestParam(required = false) List<Integer> ids);
}
