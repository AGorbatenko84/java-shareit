package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);


    void deleteItem(Long userId, Long itemId);

    ItemDto patchItem(Long userId, ItemDto item, Long itemId);

    List<ItemDto> getAvailableItems(Long userId, String text);

    ItemDto getByIdAndUserId(Long userId, Long itemId);

    CommentDto addNewComment(Long userId, CommentDto commentDto, Long itemId);
}
