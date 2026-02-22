package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.storage.UserRepository;

import java.util.List;

@Service
@Slf4j
@Qualifier("UserServiceExtImpl")
public class UserServiceExtImpl extends UserServiceImpl implements UserServiceExt {
    @Autowired
    public UserServiceExtImpl(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден."));

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
