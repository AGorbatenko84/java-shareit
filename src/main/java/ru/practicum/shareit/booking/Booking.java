package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;

    @FutureOrPresent(message = "Дата брони не может быть в прошлом")
    private LocalDate start;

    @Future(message = "Дата брони не может быть в прошлом")
    private LocalDate end;

    @NotBlank(message = "Мы точно бронируем какой-то Айтем")
    private Long itemId;

    @NotBlank(message = "Мы точно бронируем у кого-то")
    private Long userId;

    private StatusBooking statusBooking;


    private enum StatusBooking {
        WAITING,  //новое бронирование, ожидает одобрения
        APPROVED, //бронирование подтверждено владельцем
        REJECTED, //бронирование отклонено владельцем
        CANCELED; //бронирование отменено создателем
    }

}
