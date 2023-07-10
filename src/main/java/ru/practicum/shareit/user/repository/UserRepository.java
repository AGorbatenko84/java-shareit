package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserRepository {
    List<User> findAll();

    List<Long> listIdsUsers();

    User save(User user);

    void deleteItemById(Long userId, Long itemId);

    User update(Long userId, User user);

    User getUserById(Long userId);

    void deleteUserById(Long userId);

    boolean isUserContains(Long userId);
}

