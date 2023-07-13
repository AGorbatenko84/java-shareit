package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Qualifier("memory")
@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> repository;

    private Long userId;

    public UserRepositoryImpl() {
        this.repository = new HashMap<>();
        this.userId = 0L;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public User save(User user) {
        userId++;
        user.setId(userId);
        repository.put(userId, user);
        return repository.get(user.getId());
    }

    @Override
    public User update(Long userId, User user) {
        if (isEmailUsed(userId, user.getEmail())) {
            throw new ConflictException("Такая почта уже используется");
        }
        repository.put(userId, user);
        return repository.get(userId);
    }

    @Override
    public Optional<User> getUserById(Long userId) {

        return Optional.ofNullable(repository.get(userId));
    }

    @Override
    public void deleteItemById(Long userId, Long itemId) {
        repository.get(userId).getItemsId().remove(itemId);
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.remove(userId);
    }

    @Override
    public boolean isUserContains(Long userId) {
        return repository.containsKey(userId);
    }

    private boolean isEmailUsed(Long userId, String email) {
        return repository.values()
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

}
