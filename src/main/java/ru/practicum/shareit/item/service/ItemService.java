package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoReqCreate;
import ru.practicum.shareit.item.dto.ItemDtoReqPatch;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto addNewItem(Long userId, ItemDtoReqCreate item);


    void deleteItem(Long userId, Long itemId);

    ItemDto patchItem(Long userId, ItemDtoReqPatch itemDto, Long itemId);

    List<ItemDto> searchItemsByText(Long userId, String text);

    ItemDto getByIdAndUserId(Long userId, Long itemId);

    CommentDto addNewComment(Long userId, CommentDto commentDto, Long itemId);
}
