package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
}
