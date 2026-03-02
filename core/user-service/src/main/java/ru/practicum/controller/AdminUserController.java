package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.AdminUserParam;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;

import static ru.practicum.util.Constants.USER_BASE_PATH;
import static ru.practicum.util.Constants.USER_PATH_USER_ID;

@RestController
@RequestMapping(USER_BASE_PATH)
@Validated
@Slf4j
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(@Qualifier("UserServiceImpl") UserService userService) {
        this.userService = userService;
    }

    // POST /admin/users — добавить нового пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("ADMIN: create user {}", newUserRequest.getEmail());

        return userService.create(newUserRequest);
    }

    // GET /admin/users — получить список по ids или пагинацией (from/size)
    @GetMapping
    public List<UserDto> getUsers(@Valid @ModelAttribute AdminUserParam params) {
        log.info("ADMIN: get users params={}", params);

        return userService.getUsers(params);
    }

    // DELETE /admin/users/{userId}
    @DeleteMapping(USER_PATH_USER_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Integer userId) {
        log.info("ADMIN: delete user id={}", userId);

        userService.delete(userId);
    }
}
