package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.AdminUserParam;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        log.info("Перешли в сервис создания пользователя: {}", newUserRequest);

        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("User with email %s already exists."
                    .formatted(newUserRequest.getEmail()));
        }
        User user = UserMapper.toUser(newUserRequest);

        log.info("Перед записью пользователя: {}", user);

        User newUser = userRepository.save(user);

        log.info("Записан пользователь: {}", newUser);

        return UserMapper.toDto(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(AdminUserParam param) {
        // если ids переданы — возвращаем конкретных пользователей без пагинации
        if (param.getIds() != null && !param.getIds().isEmpty())
            return userRepository.findAllById(param.getIds()).stream()
                    .map(UserMapper::toDto)
                    .toList();

        // from/size -> page/size
        int from = param.getFrom() == null ? 0 : param.getFrom();
        int size = param.getSize() == null ? 10 : param.getSize();
        int page = from / size;

        return userRepository.findAll(PageRequest.of(page, size))
                .map(UserMapper::toDto)
                .getContent();
    }

    @Override
    public void delete(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("User with id = %d was not found.".formatted(userId));

        userRepository.deleteById(userId);
    }
}
