package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class BookingDtoReqCreate {
    @FutureOrPresent(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Дата брони не может быть в прошлом")
    @NotNull
    private LocalDateTime end;

    @NotNull(message = "Мы точно бронируем какой-то Айтем")
    private Long itemId;

    @AssertTrue(message = "Конец аренды должен быть до начала")
    public boolean isEndAfterStart(){
        return start == null || end == null || end.isAfter(start);
    }

    @AssertTrue(message = "Конец аренды не может быть равен началу")
    public boolean isEndEqualsStart(){
        return end == null || !end.equals(start);
    }
}

