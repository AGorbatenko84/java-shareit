package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void create_whenInvoked_thenStatusIsCreatedAndReturnedUserDto() {
        UserDto dto = getTestUserDto();
        when(userService.addNewUser(dto)).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void create_whenUserIsNotValid_thenReturnedBadRequest() {
        UserDto dto = new UserDto();
        dto.setName(null);
        dto.setEmail("jdoe@mail.com");
        when(userService.addNewUser(dto)).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        verify(userService, never()).addNewUser(dto);
    }

    @SneakyThrows
    @Test
    void patchUser_whenInvoked_thenStatusIsOkAndReturnedUserDto() {
        long userId = 1L;
        UserDto dto = getTestUserDto();

        when(userService.patchUser(userId, dto)).thenReturn(dto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserFound_thenReturnedUserDto() {
        long userId = 1L;
        UserDto dto = getTestUserDto();

        when(userService.getUserById(userId)).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersFound_thenReturnedListOfUsersDto() {
        List<UserDto> dtoList = List.of(getTestUserDto());

        when(userService.getAllUsers()).thenReturn(dtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void deleteById_whenUserFound_thenStatusIsOk() {
        long userId = 1L;
        //ResponseEntity<Object> response = ResponseEntity.status(200).build();
        when(userService.deleteUserById(userId)).thenReturn(getTestUserDto());

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(userId);
    }

    UserDto getTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("JohnDoe");
        dto.setEmail("jdoe@mail.com");
        return dto;
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}