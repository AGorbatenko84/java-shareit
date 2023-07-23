package ru.practicum.shareit.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        validationUser(userDto);
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public UserDto patchUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }
        user = UserMapper.toUser(userDto);
        user.setId(userId);
        userDto.setId(userId);
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    private void validationUser(UserDto userDto) {
        String name = userDto.getName();
        if (name == null || name.isBlank())
            throw new ValidationException("Имя пользователя не может быть пустым");
        String email = userDto.getEmail();
        if (email == null || email.isBlank())
            throw new ValidationException("Почта не может быть пуста");
        List<User> list = userRepository.findAll();
    }
}