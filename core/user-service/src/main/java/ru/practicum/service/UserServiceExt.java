package ru.practicum.service;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserServiceExt {
    UserDto getUserById(Integer userId);

    List<UserDto> getUsersByIds(List<Integer> ids);
}
