package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable().booleanValue());
        itemDto.setId(item.getId());
        itemDto.setUserId(item.getUserId());
        if (item.getRequest() != null) {
            itemDto.setRequest(item.getRequest());
        }

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable().booleanValue());
        item.setId(itemDto.getId());
        item.setUserId(itemDto.getUserId());
        return item;
    }

}