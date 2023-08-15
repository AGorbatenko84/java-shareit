package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReqCreate;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void add_whenCorrectCreateBookingDto_thenReturnedSavedBookingDto() {
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();
        BookingDto savedBookingDto = getBookingDto();
        savedBookingDto.setStart(createBookingDto.getStart());
        savedBookingDto.setEnd(createBookingDto.getEnd());
        long userId = 1L;
        when(bookingService.addNewBooking(userId, createBookingDto)).thenReturn(savedBookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedBookingDto)));
    }

    @SneakyThrows
    @Test
    void patchBooking_whenApproved_thenReturnedUpdatedBookingDto() {
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDto dto = getBookingDto();
        dto.setStatus(StatusBooking.APPROVED);
        when(bookingService.patchBooking(ownerId, bookingId, "true")).thenReturn(dto);

        mockMvc.perform(patch("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getById_whenBookingFound_thenReturnedBookingDto() {
        long bookingId = 1L;
        long userId = 1L;
        BookingDto dto = getBookingDto();
        ResponseEntity<Object> response = ResponseEntity.status(200).body(dto);
        when(bookingService.getByIdAndUserId(bookingId, userId)).thenReturn(dto);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getBookings_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        long userId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        when(bookingService.getListBookings(userId, state, from, size)).thenReturn(dtoList);

        mockMvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getBookingsOwner_whenStateIsCorrect_thenReturnedListOfBookingDtos() {
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        long ownerId = 1L;
        List<BookingDto> dtoList = List.of(getBookingDto());
        when(bookingService.getListBookingsOwner(ownerId, state, from, size)).thenReturn(dtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", String.valueOf(state))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    BookingDtoReqCreate getCreateBookingDto() {
        BookingDtoReqCreate dto = new BookingDtoReqCreate();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(2L));
        dto.setEnd(LocalDateTime.now().plusDays(3L));
        return dto;
    }

    BookingDto getBookingDto() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(StatusBooking.WAITING);
        return dto;
    }

}