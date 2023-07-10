package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
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
    public UserDto addNewUser(@Valid UserDto userDto) {
        validationUser(userDto);
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public UserDto patchUser(Long userId, UserDto userDto) {
        if (userRepository.listIdsUsers().contains(userId)) {
            User user = userRepository.getUserById(userId);
            if (userDto.getName() == null) {
                userDto.setName(user.getName());
            }
            if (userDto.getEmail() == null) {
                userDto.setEmail(user.getEmail());
            }
            user = UserMapper.toUser(userDto);
            user.setId(userId);
            userDto.setId(userId);
            userRepository.update(userId, user);
        } else throw new NotFoundException("Такого пользователя не существует");
        return userDto;
    }

    @Override
    public UserDto getUserById(Long userId) {
        UserDto userDto = new UserDto();
        if (userRepository.listIdsUsers().contains(userId)) {
            userDto = UserMapper.toUserDto(userRepository.getUserById(userId));
        } else throw new NotFoundException("Такого пользователя не существует");
        return userDto;
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
        ;
        userRepository.deleteUserById(userId);
    }

    private void validationUser(UserDto userDto) {
        String name = userDto.getName();
        if (name == null || name.isBlank())
            throw new ValidationException("Имя пользователя не может быть пустым");
        String email = userDto.getEmail();
        if (email == null || email.isBlank())
            throw new ValidationException("Почта не может быть пуста");
        List<User> list = userRepository.findAll();
        for (User u : list) {
            if (email.equalsIgnoreCase(u.getEmail()))
                throw new ValidationException("Пользователь с такой почтой уже существует");
        }
    }
}