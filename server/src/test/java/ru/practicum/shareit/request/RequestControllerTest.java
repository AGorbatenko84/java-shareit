package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoCreate;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;

    @SneakyThrows
    @Test
    void createRequest_whenInvoked_thenReturnedSavedRequestDto() {
        long userId = 1L;
        RequestDtoCreate requestDto = new RequestDtoCreate();
        requestDto.setDescription("TestDescription");
        RequestDto savedDto = getRequestDto();
        when(requestService.addNewRequest(userId, requestDto)).thenReturn(savedDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void getById_whenRequestFound_thenReturnedRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        RequestDto dto = getRequestDto();
        when(requestService.getRequestById(requestId, userId)).thenReturn(dto);

        mockMvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
        verify(requestService).getRequestById(userId, requestId);
    }

    @SneakyThrows
    @Test
    void getRequests_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        List<RequestDto> dtoList = List.of(getRequestDto());
        when(requestService.getRequests(userId)).thenReturn(dtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
        verify(requestService).getRequests(userId);
    }

    @SneakyThrows
    @Test
    void getBookingsOwner_whenRequestsFound_thenReturnedListOfRequestDtos() {
        long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<RequestDto> dtoList = List.of(getRequestDto());
        when(requestService.getAllRequests(userId, from, size)).thenReturn(dtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
        verify(requestService).getAllRequests(userId, from, size);
    }

    RequestDto getRequestDto() {
        RequestDto dto = new RequestDto();
        dto.setId(1L);
        dto.setDescription("TestDescription");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }
}