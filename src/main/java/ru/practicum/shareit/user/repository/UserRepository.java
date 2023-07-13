package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
    List<User> findAll();

    User save(User user);

    void deleteItemById(Long userId, Long itemId);

    User update(Long userId, User user);

    Optional<User> getUserById(Long userId);

    void deleteUserById(Long userId);

    boolean isUserContains(Long userId);
}

