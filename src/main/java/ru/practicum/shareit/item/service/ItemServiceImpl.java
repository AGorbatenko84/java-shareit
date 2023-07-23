package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
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
        User user = validateUserId(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setUser(user);
        itemRepository.save(item);

        itemDto = ItemMapper.toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto patchItem(Long userId, ItemDto itemDto, Long itemId) {
        User user = validateUserId(userId);
        itemDto.setId(itemId);
        Item item = itemRepository.findById(itemId).get();
        if (itemDto.getName() == null) {
            itemDto.setName(item.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(item.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(item.getAvailable());
        }
        item = ItemMapper.toItem(itemDto);
        item.setUser(user);
        itemRepository.save(item);
        return itemDto;
    }

    @Override
    public ItemDto getByIdAndUserId(Long userId, Long itemId) {
        validateUserId(userId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        Item item = optionalItem.get();
        /*if (item.getUser().getId() == userId){
        bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter()
            return ItemMapper.toItemDto(item);
        } else {*/
            return ItemMapper.toItemDto(item);
        //}
    }

    @Override
    public List<ItemDto> getAvailableItems(Long userId, String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        List<ItemDto> listItems = itemRepository.findAll()
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
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto addNewComment(Long userId, CommentDto commentDto, Long itemId) {
        validateUserId(userId);
        List<Booking> listBooking = bookingRepository.findAll().stream()
                .filter(booking -> booking.getBooker().getId().equals(userId) &&
                        booking.getItem().getId().equals(itemId))
                .collect(Collectors.toUnmodifiableList());
        if (!listBooking.isEmpty()){
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(itemRepository.findById(itemId).get());
            commentRepository.save(comment);
            CommentMapper.toCommentDto(comment);
            commentDto.setItemId(itemId);
        } else throw new NotFoundException("Нет брони на эту вещь");

        return commentDto;
    }

    private User validateUserId(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return optionalUser.get();
    }
}
