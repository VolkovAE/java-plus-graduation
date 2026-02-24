package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.user.UserClient;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserServiceExt;

import java.util.List;

import static ru.practicum.util.Constants.*;

@RestController
@RequestMapping(path = PATH_BOX_USERS)
@Validated
@Slf4j
public class UserController implements UserClient {
    private final UserServiceExt userService;

    @Autowired
    public UserController(UserServiceExt userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping(PATH_BOX_USERS_ID)
    public UserDto getUserById(@PathVariable @NotNull Integer userId) {
        return userService.getUserById(userId);
    }

    @Override
    @GetMapping(PATH_BOX_USERS_LIST)
    public List<UserDto> getUsersByIds(@RequestParam(required = false) List<Integer> ids) {
        return userService.getUsersByIds(ids);
    }
}
