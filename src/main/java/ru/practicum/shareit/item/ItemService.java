package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

interface ItemService {

    List<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);


    void deleteItem(Long userId, Long itemId);

    ItemDto patchItem(Long userId, ItemDto item);

    List<ItemDto> getAvailableItems(String text);

    ItemDto getById(Long itemId);
}
