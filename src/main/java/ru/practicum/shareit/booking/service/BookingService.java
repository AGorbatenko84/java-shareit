package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReqCreate;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long userId, BookingDtoReqCreate bookingDto);

    BookingDto patchBooking(Long userId, Long bookingId, String status);

    BookingDto getByIdAndUserId(Long userId, Long bookingId);

    List<BookingDto> getListBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getListBookingsOwner(Long userId, String state, Integer from, Integer size);
}
