package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql("/bookingRepository_test_data.sql")
class BookingRepositoryTest {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort SORT_BY_START_ASC = Sort.by(Sort.Direction.ASC, "start");
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10, SORT_BY_START_DESC);

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findFirstByItemIdAndStartLessThanEqual() {
        Booking booking = getBooking();
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));
        booking.setStatus(StatusBooking.APPROVED);
        Booking expectedBooking = entityManager.persistAndFlush(booking);

        Booking actualBooking = bookingRepository.findFirstByItemIdAndStartLessThanEqual(
                1L, LocalDateTime.now(), SORT_BY_START_DESC);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void findFirstByItemIdAndStartAfter() {
        Booking booking = getBooking();
        booking.setStart(LocalDateTime.now().plusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        booking.setStatus(StatusBooking.APPROVED);
        Booking expectedBooking = entityManager.persistAndFlush(booking);

        Booking actualBooking = bookingRepository.findFirstByItemIdAndStartAfter(
                1L, LocalDateTime.now(), SORT_BY_START_ASC);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void findByItemInAndStartLessThanEqual() {
        List<Item> items = itemRepository.findAll();
        Booking booking = getBooking();
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));
        booking.setStatus(StatusBooking.APPROVED);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByItemInAndStartLessThanEqual(
                items, LocalDateTime.now(), SORT_BY_START_DESC);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByItemInAndStartAfter() {
        List<Item> items = itemRepository.findAll();
        Booking booking = getBooking();
        booking.setStart(LocalDateTime.now().plusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        booking.setStatus(StatusBooking.APPROVED);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByItemInAndStartAfter(
                items, LocalDateTime.now(), SORT_BY_START_ASC);

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByBookerId() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByBookerId(1L, PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfter() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(1L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                1L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByBookerIdAndStartIsAfterAndEndIsAfter() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByBookerIdAndStartIsAfterAndEndIsAfter(
                1L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsBefore() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsBefore(
                1L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByBookerIdAndStatusIs() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.REJECTED);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByBookerIdAndStatusIs(
                1L, StatusBooking.REJECTED, PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwnerId() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByOwnerId(2L, PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwnerIdAndStartIsBeforeAndEndIsAfter() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(1L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsAfter(
                2L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwnerIdAndStartIsAfterAndEndIsAfter() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByOwnerIdAndStartIsAfterAndEndIsAfter(
                2L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwnerIdAndStartIsBeforeAndEndIsBefore() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.WAITING);
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByOwnerIdAndStartIsBeforeAndEndIsBefore(
                2L, LocalDateTime.now(), LocalDateTime.now(), PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByOwnerIdAndStatusIs() {
        Booking booking = getBooking();
        booking.setStatus(StatusBooking.REJECTED);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByOwnerIdAndStatusIs(
                2L, StatusBooking.REJECTED, PAGE_REQUEST).getContent();

        assertEquals(expectedBookings, actualBookings);
    }

    @Test
    void findByItem_IdAndBooker_IdAndStatusAndEndIsBefore() {
        Booking booking = getBooking();
        booking.setStart(LocalDateTime.now().minusDays(2L));
        booking.setEnd(LocalDateTime.now().minusDays(1L));
        booking.setStatus(StatusBooking.APPROVED);
        Booking savedBooking = entityManager.persistAndFlush(booking);
        List<Booking> expectedBookings = List.of(savedBooking);

        List<Booking> actualBookings = bookingRepository.findByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                1L, 1L, StatusBooking.APPROVED, LocalDateTime.now());

        assertEquals(expectedBookings, actualBookings);
    }

    private Booking getBooking() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1L));
        Optional<User> booker = userRepository.findById(1L);
        booker.ifPresent(booking::setBooker);
        Optional<User> owner = userRepository.findById(2L);
        owner.ifPresent(booking::setOwner);
        Optional<Item> item = itemRepository.findById(1L);
        item.ifPresent(booking::setItem);
        return booking;
    }
}