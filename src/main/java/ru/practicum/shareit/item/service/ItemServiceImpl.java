package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoReqCreate;
import ru.practicum.shareit.item.dto.ItemDtoReqPatch;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Sort SORT_BY_START_ASC = Sort.by(Sort.Direction.ASC, "start");
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;


    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByUserIdOrderByIdAsc(userId);
        LocalDateTime timeNow = LocalDateTime.now();
        Map<Long, List<Comment>> comments = commentRepository.findByItemIn(items, SORT_BY_CREATED_DESC)
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));

        Map<Long, List<Booking>> lastBookingsMap = bookingRepository.findByItemInAndStartLessThanEqual(
                        items, timeNow, SORT_BY_START_DESC)
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        Map<Long, List<Booking>> nextBookingsMap = bookingRepository.findByItemInAndStartAfter(
                        items, timeNow, SORT_BY_START_ASC)
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        List<ItemDto> itemDtoList = itemMapper.toItemDtoList(items);
        for (ItemDto itemDto : itemDtoList) {
            List<Booking> lastBookings = lastBookingsMap.get(itemDto.getId());
            List<Booking> nextBookings = nextBookingsMap.get(itemDto.getId());
            List<Comment> commentList = comments.get(itemDto.getId());
            if (Objects.nonNull(lastBookings) && !lastBookings.isEmpty()) {
                itemDto.setLastBooking(bookingMapper.toBookingInItemDto(lastBookings.get(0)));
            }
            if (Objects.nonNull(nextBookings) && !nextBookings.isEmpty()) {
                if (nextBookings.get(0).getStatus().equals(StatusBooking.APPROVED)) {
                    itemDto.setNextBooking(bookingMapper.toBookingInItemDto(nextBookings.get(0)));
                }
            }
            if (Objects.nonNull(commentList)) {
                itemDto.setComments(commentMapper.toDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto addNewItem(Long userId, ItemDtoReqCreate itemDto) {
        User user = validateUserId(userId);
        ;
        Item item = itemMapper.toItem(itemDto);
        item.setUser(user);
        Long requestId = itemDto.getRequestId();
        if (Objects.nonNull(requestId)) {
            Request itemRequest = requestRepository.findById(requestId).orElseThrow(() -> {
                throw new NotFoundException("Request with id " + requestId + " not found");
            });
            item.setRequest(itemRequest);
        }
        itemRepository.save(item);

        ItemDto itemDtoResp = itemMapper.toItemDto(item);
        return itemDtoResp;
    }

    @Override
    public ItemDto patchItem(Long userId, ItemDtoReqPatch itemDto, Long itemId) {
        User user = validateUserId(userId);
        itemDto.setId(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Такого предмета не существует");
        });
        if (!item.getUser().getId().equals(userId)) {
            throw new ValidationException("Этот пользователь не может внести изменения.");
        }
        if (itemDto.getName() == null) {
            itemDto.setName(item.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(item.getDescription());
        }

        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(item.getAvailable());
        }
        itemMapper.updateItem(itemDto, item);
        item.setUser(user);
        itemRepository.save(item);
        ItemDto itemDtoResp = itemMapper.toItemDto(item);
        return itemDtoResp;
    }

    @Override
    public ItemDto getByIdAndUserId(Long userId, Long itemId) {
        validateUserId(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Такой вещи не существует");
        });
        ItemDto itemDto = itemMapper.toItemDto(item);
        LocalDateTime timeNow = LocalDateTime.now();
        if (item.getUser().getId().equals(userId)) {
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartLessThanEqual(itemId, timeNow, SORT_BY_START_DESC);
            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfter(itemId, timeNow, SORT_BY_START_ASC);
            itemDto.setLastBooking(bookingMapper.toBookingInItemDto(lastBooking));
            if (nextBooking != null && nextBooking.getStatus().equals(StatusBooking.APPROVED)) {
                itemDto.setNextBooking(bookingMapper.toBookingInItemDto(nextBooking));
            }
        }
        List<CommentDto> commentDtoList = commentMapper.toDtoList(commentRepository.findAllByItemId(itemId));
        itemDto.setComments(commentDtoList);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItemsByText(Long userId, String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        List<ItemDto> listItems = itemMapper.toItemDtoList(itemRepository.search(text));
        return listItems;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto addNewComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = validateUserId(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Такой вещи не существует");
        });
        LocalDateTime timeNow = LocalDateTime.now();
        List<Booking> listBooking = bookingRepository
                .findByItem_IdAndBooker_IdAndStatusAndEndIsBefore(itemId, userId, StatusBooking.APPROVED, timeNow);
        if (!listBooking.isEmpty()) {
            Comment comment = commentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(timeNow);
            commentRepository.save(comment);
            commentDto = commentMapper.toCommentDto(comment);
            //commentDto.setItemId(itemId);
        } else throw new ValidationException("Нет брони на эту вещь");

        return commentDto;
    }

    private User validateUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Такого пользователя не существует");
        });
        return user;
    }
}
