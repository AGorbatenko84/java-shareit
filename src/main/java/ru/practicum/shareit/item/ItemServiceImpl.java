package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.findByUserId(userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }


    @Override
    public ItemDto addNewItem(Long userId, @Valid ItemDto itemDto){
        if (!userRepository.isUserContains(userId)) {
            throw new NotFoundException("Пользователя с таким Id не существует");

        }

            Item item = ItemMapper.toItem(itemDto);
            userRepository.getUserById(userId).addIdItem(item.getId());

            itemRepository.save(userId, item);
            itemDto.setId(item.getId());

        return itemDto;
    }

    @Override
    public ItemDto patchItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        itemRepository.getAllItems().put(item.getId(),item);
        return itemDto;
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getAllItems().get(itemId));
    }

    @Override
    public List<ItemDto> getAvailableItems(String text) {
        return null;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByItemId(itemId);
        userRepository.deleteItemById(userId, itemId);
    }

}
