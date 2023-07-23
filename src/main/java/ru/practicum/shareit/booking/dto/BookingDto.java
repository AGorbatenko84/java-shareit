package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.service.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @Future(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime end;

    @NotNull(message = "Мы точно бронируем какой-то Айтем")
    private Long itemId;
    private ItemDto item;
    private StatusBooking status;
    private UserDto booker;
}
