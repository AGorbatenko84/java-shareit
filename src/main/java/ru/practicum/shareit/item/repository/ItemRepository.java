package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Map<Long, Item> getAllItems();

    List<Item> findByUserId(Long userId);

    Item save(Long userId, Item item);

    void deleteByItemId(Long itemId);
}
