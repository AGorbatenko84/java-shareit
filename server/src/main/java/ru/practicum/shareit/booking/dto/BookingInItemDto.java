package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;

@Data
public class BookingInItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusBooking status;
    private Long bookerId;
}
