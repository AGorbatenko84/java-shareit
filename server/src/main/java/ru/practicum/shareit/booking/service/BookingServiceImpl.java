package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addNewBooking(Long userId, BookingDtoReqCreate bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с таким id отсутствует");
        });
        Booking booking = bookingMapper.toBooking(bookingDto);
        Item item = validateItem(userId, bookingDto.getItemId());
        booking.setItem(item);
        booking.setStatus(StatusBooking.WAITING);
        booking.setOwner(item.getUser());
        if (!item.getUser().getId().equals(userId)) {
            booking.setBooker(userRepository.getById(userId));
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto patchBooking(Long userId, Long bookingId, String status) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с таким id отсутствует");
        });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Такой брони не существует");
        });

        if (!userId.equals(booking.getOwner().getId())) {
            throw new NotFoundException("Не возможно изменить статус");
        }
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
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такой пользователь отсутствует");
        });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Такой брони не существует");
        });
        if (!userId.equals(booking.getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            throw new NotFoundException("Запрос может делать или владелец или автор брони");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getListBookings(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такой id отсутствует");
        });
        PageRequest pageRequest = PageRequest.of((from / size), size, SORT_BY_START_DESC);
        Page<Booking> page = Page.empty();
        LocalDateTime currentTime = LocalDateTime.now();
        if (state == null) {
            state = "ALL";
        }
        switch (state.toUpperCase()) {
            case "ALL":
                page = bookingRepository.findByBookerId(userId, pageRequest);
                break;
            case "CURRENT":
                page = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "FUTURE":
                page = bookingRepository.findByBookerIdAndStartIsAfterAndEndIsAfter(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "PAST":
                page = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "WAITING":
                page = bookingRepository.findByBookerIdAndStatusIs(userId, StatusBooking.WAITING, pageRequest);
                break;
            case "REJECTED":
                page = bookingRepository.findByBookerIdAndStatusIs(userId, StatusBooking.REJECTED, pageRequest);
                break;
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingMapper.toBookingDtoList(page.getContent());
    }

    @Override
    public List<BookingDto> getListBookingsOwner(Long userId, String state, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такой id отсутствует");
        });

        PageRequest pageRequest = PageRequest.of((from / size), size, SORT_BY_START_DESC);
        Page<Booking> page = Page.empty();

        if (state == null) {
            state = "ALL";
        }
        LocalDateTime currentTime = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                page = bookingRepository.findByOwnerId(userId, pageRequest);
                break;
            case "CURRENT":
                page = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "FUTURE":
                page = bookingRepository.findByOwnerIdAndStartIsAfterAndEndIsAfter(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "PAST":
                page = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsBefore(
                        userId, currentTime, currentTime, pageRequest);
                break;
            case "WAITING":
                page = bookingRepository.findByOwnerIdAndStatusIs(userId, StatusBooking.WAITING, pageRequest);
                break;
            case "REJECTED":
                page = bookingRepository.findByOwnerIdAndStatusIs(userId, StatusBooking.REJECTED, pageRequest);
                break;
            default:
                throw new StatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingMapper.toBookingDtoList(page.getContent());
    }

    private Item validateItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Предмет с таким id отсутствует");
        });
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь сейчас не доступна");
        }
        if (item.getUser().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        return item;
    }
}
