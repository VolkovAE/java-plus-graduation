package ru.practicum.service;

import ru.practicum.dto.user.AdminUserParam;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest request);

    List<UserDto> getUsers(AdminUserParam param);

    void delete(Integer userId);
}
