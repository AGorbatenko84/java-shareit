package ru.practicum.shareit.booking.service;

public enum StatusBooking {
    WAITING,  //новое бронирование, ожидает одобрения
    APPROVED, //бронирование подтверждено владельцем
    REJECTED, //бронирование отклонено владельцем
    CANCELED; //бронирование отменено создателем
}
