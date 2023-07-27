package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingDtoReqCreate {
    @Future(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime end;

    @NotNull(message = "Мы точно бронируем какой-то Айтем")
    private Long itemId;
}
