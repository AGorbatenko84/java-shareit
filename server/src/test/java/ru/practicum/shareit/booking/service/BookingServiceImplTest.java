package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReqCreate;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    @InjectMocks
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void addNewBooking_whenInvoked_thenSavedUser() {
        Long userId = 2L;
        Item expectedItem = getItem();
        User expectedUser = getUser();
        expectedUser.setId(2L);
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();

        Booking bookingToSave = bookingMapper.toBooking(createBookingDto);
        bookingToSave.setId(1L);
        bookingToSave.setItem(expectedItem);
        bookingToSave.setBooker(expectedUser);
        bookingToSave.setStatus(StatusBooking.WAITING);
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(expectedItem));
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        doAnswer(invocationOnMock -> {
            Booking booking = invocationOnMock.getArgument(0, Booking.class);
            booking.setId(1L);
            return booking;
        }).when(bookingRepository).save(any(Booking.class));
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToSave);
        expectedBookingDto.setId(1L);

        BookingDto actualBookingDto = bookingService.addNewBooking(userId, createBookingDto);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(bookingToSave, savedBooking);
    }

    @Test
    void addNewBooking_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUser()));
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(userId, createBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNewBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(userId, createBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNewBooking_whenOwnerCreateBooking_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();
        Item item = getItem();
        when(userRepository.findById(userId)).thenReturn(Optional.of(getUser()));
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.addNewBooking(userId, createBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addNewBooking_whenItemUnavailable_thenItemUnavailableExceptionThrown() {
        Long userId = 2L;
        BookingDtoReqCreate createBookingDto = getCreateBookingDto();
        Item item = getItem();
        item.setAvailable(Boolean.FALSE);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(createBookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.addNewBooking(userId, createBookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void patchBooking_whenApproved_thenStatusApprovedSet() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking bookingToUpdate = getBooking();
        User owner = new User();
        owner.setId(ownerId);
        bookingToUpdate.setOwner(owner);
        bookingToUpdate.setStatus(StatusBooking.WAITING);
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToUpdate);
        expectedBookingDto.setStatus(StatusBooking.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        BookingDto actualBookingDto = bookingService.patchBooking(ownerId, bookingId, "TRUE");

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void patchBooking_whenRejected_thenStatusRejectedSet() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking bookingToUpdate = getBooking();
        User owner = new User();
        owner.setId(ownerId);
        bookingToUpdate.setOwner(owner);
        bookingToUpdate.setStatus(StatusBooking.WAITING);
        BookingDto expectedBookingDto = bookingMapper.toBookingDto(bookingToUpdate);
        expectedBookingDto.setStatus(StatusBooking.REJECTED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        BookingDto actualBookingDto = bookingService.patchBooking(ownerId, bookingId, "false");

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void patchBooking_whenStatusAlreadySet_thenBookingStatusExceptionThrown() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        User owner = getUser();
        Booking bookingToUpdate = getBooking();
        bookingToUpdate.setStatus(StatusBooking.APPROVED);
        bookingToUpdate.setOwner(owner);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(ValidationException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, "TRUE"));
    }

    @Test
    void patchBooking_whenNotOwnerSetStatus_thenNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long ownerId = 3L;
        Booking bookingToUpdate = getBooking();
        bookingToUpdate.setStatus(StatusBooking.WAITING);
        bookingToUpdate.setOwner(getUser());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToUpdate));

        assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, "TRUE"));
    }

    @Test
    void patchBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(ownerId, bookingId, "TRUE"));
    }

    @Test
    void getByIdAndUserId_whenBookingFoundAndBooker_thenReturnedBooking() {
        Long bookingId = 1L;
        Long userId = 2L;
        User user = getUser();
        user.setId(userId);
        Booking foundedBooking = getBooking();
        foundedBooking.setOwner(user);
        BookingDto expectedDto = bookingMapper.toBookingDto(foundedBooking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        BookingDto actualDto = bookingService.getByIdAndUserId(userId, bookingId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getByIdAndUserId_whenBookingFoundAndItemOwner_thenReturnedBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        Long ownerId = 2L;
        User owner = getUser();
        owner.setId(ownerId);
        Booking foundedBooking = getBooking();
        foundedBooking.setOwner(owner);
        BookingDto expectedDto = bookingMapper.toBookingDto(foundedBooking);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(getUser()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        BookingDto actualDto = bookingService.getByIdAndUserId(ownerId, bookingId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getByIdAndUserId_whenBookingFoundAndAnotherUser_thenNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long userId = 0L;
        Booking foundedBooking = getBooking();
        foundedBooking.setOwner(getUser());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUser()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundedBooking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getByIdAndUserId(userId, bookingId));
    }

    @Test
    void getByIdAndUserId_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 0L;
        Long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(getUser()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getByIdAndUserId(userId, bookingId));
    }

    @Test
    void getListBookings() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Booking booking = getBooking();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> expectedDtos = bookingMapper.toBookingDtoList(bookings);
        PageRequest pageRequest = PageRequest.of(
                (from / size), size, SORT_BY_START_DESC);
        Page<Booking> page = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerId(userId, pageRequest)).thenReturn(page);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByBookerIdAndStartIsAfterAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByBookerIdAndStatusIs(userId, StatusBooking.WAITING, pageRequest)).thenReturn(page);
        when(bookingRepository.findByBookerIdAndStatusIs(userId, StatusBooking.REJECTED, pageRequest)).thenReturn(page);

        List<BookingDto> actualDtosAll = bookingService.getListBookings(userId, "ALL", from, size);
        List<BookingDto> actualDtosCurrent = bookingService.getListBookings(userId, "CURRENT", from, size);
        List<BookingDto> actualDtosFuture = bookingService.getListBookings(userId, "FUTURE", from, size);
        List<BookingDto> actualDtosPast = bookingService.getListBookings(userId, "PAST", from, size);
        List<BookingDto> actualDtosWaiting = bookingService.getListBookings(userId, "WAITING", from, size);
        List<BookingDto> actualDtosRejected = bookingService.getListBookings(userId, "REJECTED", from, size);

        assertEquals(expectedDtos, actualDtosAll);
        assertEquals(expectedDtos, actualDtosCurrent);
        assertEquals(expectedDtos, actualDtosFuture);
        assertEquals(expectedDtos, actualDtosPast);
        assertEquals(expectedDtos, actualDtosWaiting);
        assertEquals(expectedDtos, actualDtosRejected);
        assertThrows(StatusException.class,
                () -> bookingService.getListBookings(userId, "OTHER", from, size));
    }

    @Test
    void getListBookingsOwner() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Booking booking = getBooking();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> expectedDtos = bookingMapper.toBookingDtoList(bookings);
        PageRequest pageRequest = PageRequest.of(
                (from / size), size, SORT_BY_START_DESC);
        Page<Booking> page = new PageImpl<>(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByOwnerId(userId, pageRequest)).thenReturn(page);
        when(bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByOwnerIdAndStartIsAfterAndEndIsAfter(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsBefore(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageRequest))).thenReturn(page);
        when(bookingRepository.findByOwnerIdAndStatusIs(userId, StatusBooking.WAITING, pageRequest)).thenReturn(page);
        when(bookingRepository.findByOwnerIdAndStatusIs(userId, StatusBooking.REJECTED, pageRequest)).thenReturn(page);

        List<BookingDto> actualDtosAll = bookingService.getListBookingsOwner(userId, "ALL", from, size);
        List<BookingDto> actualDtosCurrent = bookingService.getListBookingsOwner(userId, "CURRENT", from, size);
        List<BookingDto> actualDtosFuture = bookingService.getListBookingsOwner(userId, "FUTURE", from, size);
        List<BookingDto> actualDtosPast = bookingService.getListBookingsOwner(userId, "PAST", from, size);
        List<BookingDto> actualDtosWaiting = bookingService.getListBookingsOwner(userId, "WAITING", from, size);
        List<BookingDto> actualDtosRejected = bookingService.getListBookingsOwner(userId, "REJECTED", from, size);

        assertEquals(expectedDtos, actualDtosAll);
        assertEquals(expectedDtos, actualDtosCurrent);
        assertEquals(expectedDtos, actualDtosFuture);
        assertEquals(expectedDtos, actualDtosPast);
        assertEquals(expectedDtos, actualDtosWaiting);
        assertEquals(expectedDtos, actualDtosRejected);
        assertThrows(StatusException.class,
                () -> bookingService.getListBookingsOwner(userId, "OTHER", from, size));
    }

    BookingDtoReqCreate getCreateBookingDto() {
        BookingDtoReqCreate dto = new BookingDtoReqCreate();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(3L));
        return dto;
    }

    Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(Boolean.TRUE);
        item.setUser(getUser());
        return item;
    }

    User getUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    Booking getBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(3L));
        booking.setItem(getItem());
        booking.setBooker(getUser());
        booking.getBooker().setId(2L);
        return booking;
    }
}