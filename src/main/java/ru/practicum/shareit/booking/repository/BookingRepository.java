package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findFirstByItemIdAndStartLessThanEqual(Long itemId, LocalDateTime start, Sort sort);

    Booking findFirstByItemIdAndStartAfter(Long itemId, LocalDateTime start, Sort sort);

    List<Booking> findByItemInAndStartLessThanEqual(List<Item> items, LocalDateTime start, Sort sort);

    List<Booking> findByItemInAndStartAfter(List<Item> items, LocalDateTime start, Sort sort);
}
