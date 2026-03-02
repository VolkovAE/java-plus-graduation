package ru.practicum.mapper;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

public class UserMapper {
    public static User toUser(NewUserRequest newUserRequest) {
        if (newUserRequest == null) return null;

        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        return user;
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

    public static UserShortDto toShortDto(User user) {
        if (user == null) return null;

        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }
}
