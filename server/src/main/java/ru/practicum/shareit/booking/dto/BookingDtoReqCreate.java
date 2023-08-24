package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingDtoReqCreate {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}

