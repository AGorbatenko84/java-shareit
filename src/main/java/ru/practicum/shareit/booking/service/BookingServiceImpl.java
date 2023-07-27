package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReqCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addNewBooking(Long userId, BookingDtoReqCreate bookingDto) {
        validateUserId(userId);
        Booking booking = bookingMapper.toBooking(bookingDto);
        validateDate(bookingDto);
        Item item = validateItem(userId, bookingDto.getItemId());
        booking.setItem(item);
        booking.setStatus(StatusBooking.WAITING);
        booking.setOwner(item.getUser());
        if (item.getUser().getId() != userId) {
            booking.setBooker(userRepository.getById(userId));
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto patchBooking(Long userId, Long bookingId, String status) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.get().getOwner().getId() != userId || optionalBooking.isEmpty()) {
            throw new NotFoundException("Не возможно изменить статус");
        }
        Booking booking = optionalBooking.get();
        if (status.equalsIgnoreCase("true")) {
            if (booking.getStatus().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Статус уже подтвержден");
            }
            booking.setStatus(StatusBooking.APPROVED);
        }
        if (status.equalsIgnoreCase("false")) {
            if (booking.getStatus().equals(StatusBooking.REJECTED)) {
                throw new ValidationException("Статус уже подтвержден");
            }
            booking.setStatus(StatusBooking.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getByIdAndUserId(Long userId, Long bookingId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalUser.isEmpty() || optionalBooking.isEmpty()) {
            throw new NotFoundException("Такой id отсутствует");
        }
        if (!optionalBooking.get().getOwner().getId().equals(userId)
                && !optionalBooking.get().getBooker().getId().equals(userId)) {
            throw new NotFoundException("Запрос может делать или владелец или автор брони");
        }
        return bookingMapper.toBookingDto(optionalBooking.get());
    }

    @Override
    public List<BookingDto> getListBookings(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такой id отсутствует");
        });

        List<BookingDto> bookingDtoList = Collections.emptyList();
        if (state == null) {
            state = "ALL";
        }
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .filter(booking -> booking.getStatus().toString().toUpperCase().equals("WAITING"))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .filter(booking -> booking.getStatus().toString().toUpperCase().equals("REJECTED"))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "ALL":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getBooker().getId().equals(userId))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList;
    }

    @Override
    public List<BookingDto> getListBookingsOwner(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такой id отсутствует");
        });
        List<BookingDto> bookingDtoList = Collections.emptyList();
        if (state == null) {
            state = "ALL";
        }
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getOwner().getId().equals(userId))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "PAST":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getOwner().getId().equals(userId))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getOwner().getId().equals(userId))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getOwner().getId().equals(userId))
                        .filter(booking -> booking.getStatus().toString().toUpperCase().equals("WAITING"))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookingDtoList = bookingRepository.findAll().stream()
                        .filter(booking -> booking.getOwner().getId().equals(userId))
                        .filter(booking -> booking.getStatus().toString().toUpperCase().equals("REJECTED"))
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            case "ALL":
                bookingDtoList = bookingRepository.findAll().stream()
                        .map(booking -> bookingMapper.toBookingDto(booking))
                        .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList;
    }

    private void validateUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id отсутствует");
        }
    }

    private Item validateItem(Long userId, Long itemId) {

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с таким id отсутствует");
        }
        Item item = optionalItem.get();
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь сейчас не доступна");
        }
        if (item.getUser().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        return item;
    }

    private void validateDate(BookingDtoReqCreate bookingDto) {
        LocalDateTime startTime = bookingDto.getStart();
        LocalDateTime endTime = bookingDto.getEnd();
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new ValidationException("Время начало аренды должно быть раньше времени конца аренды");
        }
    }

}
