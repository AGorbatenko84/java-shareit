package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void addNewUser_whenInvoked_ThenSavedUser() {
        User userToSave = getTestUser();
        UserDto userDtoToSave = getTestUserDto();
        when(userMapper.toUser(userDtoToSave)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.addNewUser(userDtoToSave);

        assertEquals(userDtoToSave, actualUserDto);
        verify(userRepository).save(userToSave);
    }

    @Test
    void patchUser_whenUserFound_thenUpdatedUser() {
        User oldUser = getTestUser();
        User updateUser = getUpdatedUser();
        UserDto updateUserDto = getUpdateUserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));

        when(userRepository.save(updateUser)).thenReturn(updateUser);
        when(userMapper.toUser(updateUserDto)).thenReturn(updateUser);
        UserDto actualUserDto = userService.patchUser(1L, updateUserDto);

        assertEquals(updateUserDto, actualUserDto);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(updateUser, savedUser);
    }

    @Test
    void patchUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        UserDto userDto = getTestUserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.patchUser(1L, userDto));
    }

    @Test
    void getUserById_whenUserFound_ThenReturnedUser() {
        Long userId = 1L;
        User expectedUser = getTestUser();
        UserDto expectedUserDto = getTestUserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toUserDto(expectedUser)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.getUserById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void getUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_whenInvoked_ThenReturnListOfUsers() {
        List<User> expectedUsers = List.of(getTestUser());
        List<UserDto> expectedUserDtos = List.of(getTestUserDto());
        when(userRepository.findAll()).thenReturn(expectedUsers);
        when(userMapper.toUserDtoList(expectedUsers)).thenReturn(expectedUserDtos);

        List<UserDto> actualUserDtos = userService.getAllUsers();

        assertEquals(expectedUserDtos, actualUserDtos);
    }

    @Test
    void deleteUserById_whenUserFound_ThenDeleteByIdInvoked() {
        Long userId = 1L;
        User user = getTestUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(userId));
    }

    User getTestUser() {
        return new User(1L, "TestEmail", "TestName");
    }

    UserDto getTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .email("TestEmail")
                .name("TestName")
                .build();
    }

    User getUpdatedUser() {
        return new User(1L, "UpdatedEmail", "UpdatedName");
    }

    UserDto getUpdateUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("UpdatedName")
                .email("UpdatedEmail")
                .build();
    }
}