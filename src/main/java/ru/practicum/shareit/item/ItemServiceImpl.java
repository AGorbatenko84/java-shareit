package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
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
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        if (!userRepository.isUserContains(userId)) {
            throw new NotFoundException("Пользователя с таким Id не существует");
        }
        validationItem(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        itemRepository.save(userId, item);
        userRepository.getUserById(userId).addIdItem(item.getId());

        itemDto.setId(item.getId());

        return itemDto;
    }

    @Override
    public ItemDto patchItem(Long userId, ItemDto itemDto, Long itemId) {
        itemDto.setId(itemId);
        if (!isUserContainsThisItem(userId, itemDto)) {
            throw new NotFoundException("У пользователя с таким Id нет такого предмета");
        }
        Item item = itemRepository.getItemById(itemId);
        if (itemDto.getName() == null) {
            itemDto.setName(item.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(item.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(item.getAvailable());
        }
        itemDto.setUserId(userId);

        item = ItemMapper.toItem(itemDto);
        itemRepository.update(item);

        return itemDto;
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getAllItems().get(itemId));
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        List<ItemDto> listItems = itemRepository.getAllItems().values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().booleanValue())
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
        return listItems;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByItemId(itemId);
        userRepository.deleteItemById(userId, itemId);
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || !itemDto.getAvailable().booleanValue()) {
            throw new ValidationException("При создании предмет должен быть доступен.");
        }

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("При создании предмет должен иметь название.");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("При создании предмет должен иметь описание.");
        }
    }

    private boolean isUserContainsThisItem(Long userId, ItemDto itemDto) {
        return userRepository.getUserById(userId)
                .getItemsId()
                .contains(itemDto.getId());
    }

}
