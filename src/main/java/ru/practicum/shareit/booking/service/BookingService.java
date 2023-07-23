package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long userId, BookingDto bookingDto);

    BookingDto patchBooking(Long userId, Long bookingId, String status);

    BookingDto getByIdAndUserId(Long userId, Long bookingId);

    List<BookingDto> getListBookings(Long userId, String state);

    List<BookingDto> getListBookingsOwner(Long userId, String state);
}
