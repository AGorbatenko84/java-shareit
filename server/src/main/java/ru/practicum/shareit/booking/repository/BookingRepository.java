package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findFirstByItemIdAndStartLessThanEqual(Long itemId, LocalDateTime start, Sort sort);

    Booking findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime start, Sort sort);

    List<Booking> findByItemInAndStartLessThanEqual(List<Item> items, LocalDateTime start, Sort sort);

    List<Booking> findByItemInAndStartAfter(List<Item> items, LocalDateTime start, Sort sort);

    Page<Booking> findByBookerId(Long userId, PageRequest pageRequest);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start,
                                                              LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByBookerIdAndStartIsAfterAndEndIsAfter(Long userId, LocalDateTime start,
                                                             LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsBefore(Long userId, LocalDateTime start,
                                                               LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByBookerIdAndStatusIs(Long userId, StatusBooking status, PageRequest pageRequest);

    Page<Booking> findByOwnerId(Long userId, PageRequest pageRequest);

    Page<Booking> findByOwnerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start,
                                                             LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByOwnerIdAndStartIsAfterAndEndIsAfter(Long userId, LocalDateTime start,
                                                            LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByOwnerIdAndStartIsBeforeAndEndIsBefore(Long userId, LocalDateTime start,
                                                              LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByOwnerIdAndStatusIs(Long userId, StatusBooking status, PageRequest pageRequest);

    List<Booking> findByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, StatusBooking status, LocalDateTime end);
}
