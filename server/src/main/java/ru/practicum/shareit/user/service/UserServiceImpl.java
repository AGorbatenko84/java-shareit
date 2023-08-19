package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
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
        user = userMapper.toUser(userDto);
        user.setId(userId);
        userDto.setId(userId);
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> list = userRepository.findAll();
        return userMapper.toUserDtoList(list);
    }

    @Override
    public UserDto deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
        userRepository.deleteById(userId);
        return userMapper.toUserDto(user);
    }
}