package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Test
    void addNewItem_whenRequestIsNull_thenItemSaved() {
        Long ownerId = 1L;
        User owner = getTestUser();
        Item itemToSave = getTestItem();
        ItemDtoReqCreate createItemDto = getCreateItemDto();
        ItemDto expectedItemDto = getItemDto();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(createItemDto)).thenReturn(itemToSave);
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);
        when(itemMapper.toItemDto(itemToSave)).thenReturn(expectedItemDto);

        ItemDto actualItemDto = itemService.addNewItem(ownerId, createItemDto);

        assertEquals(expectedItemDto, actualItemDto);
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void addNewItem_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        Long ownerId = 1L;
        ItemDtoReqCreate createItemDto = getCreateItemDto();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(ownerId, createItemDto));
    }

    @Test
    void addNewItem_whenRequestFound_thenItemSaved() {
        Long ownerId = 1L;
        User owner = getTestUser();
        Item itemToSave = getTestItem();
        ItemDtoReqCreate createItemDto = getCreateItemDto();
        createItemDto.setRequestId(1L);
        ItemDto expectedItemDto = getItemDto();
        expectedItemDto.setRequestId(1L);
        Request itemRequest = getTestRequest();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(createItemDto)).thenReturn(itemToSave);
        when(itemRequestRepository.findById(createItemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
        doAnswer(invocationOnMock -> {
            Item item = invocationOnMock.getArgument(0, Item.class);
            item.setId(1L);
            return item;
        }).when(itemRepository).save(itemToSave);
        when(itemMapper.toItemDto(itemToSave)).thenReturn(expectedItemDto);

        ItemDto actualItemDto = itemService.addNewItem(ownerId, createItemDto);

        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(1L, itemToSave.getId());
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void addNewItem_whenRequestNotFound_thenNotFoundExceptionThrown() {
        Long ownerId = 1L;
        User owner = getTestUser();
        Item itemToSave = getTestItem();
        ItemDtoReqCreate createItemDto = getCreateItemDto();
        createItemDto.setRequestId(0L);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(createItemDto)).thenReturn(itemToSave);
        when(itemRequestRepository.findById(createItemDto.getRequestId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(ownerId, createItemDto));
        verify(itemRepository, never()).save(itemToSave);
    }


    @Test
    void patchItem_whenItemFound_thenUpdateItem() {
        Item foundedItem = getTestItem();
        foundedItem.setId(1L);
        User owner = getTestUser();
        foundedItem.setUser(owner);
        ItemDto updatedItemDto = getUpdatedItemDto();
        ItemDtoReqPatch patchItemDto = getPatchItem();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(patchItemDto.getId())).thenReturn(Optional.of(foundedItem));
        doAnswer(invocationOnMock -> {
            ItemDtoReqPatch itemDto = invocationOnMock.getArgument(0, ItemDtoReqPatch.class);
            Item item = invocationOnMock.getArgument(1, Item.class);
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            return null;
        }).when(itemMapper).updateItem(patchItemDto, foundedItem);
        when(itemMapper.toItemDto(foundedItem)).thenReturn(updatedItemDto);

        ItemDto actualItemDto = itemService.patchItem(1L, patchItemDto, 1L);

        assertEquals(updatedItemDto, actualItemDto);
    }

    @Test
    void patchItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        ItemDtoReqPatch patchItemDto = getPatchItem();
        Long ownerId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(patchItemDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.patchItem(ownerId, patchItemDto, patchItemDto.getId()));
    }

    @Test
    void patchItem_whenNotOwnerChanges_thenValidationExceptionThrown() {
        ItemDtoReqPatch patchItemDto = getPatchItem();
        Long ownerId = 0L;
        Item foundedItem = getTestItem();
        foundedItem.setId(1L);
        User notOwner = getTestUser();
        foundedItem.setUser(notOwner);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(patchItemDto.getId())).thenReturn(Optional.of(foundedItem));

        assertThrows(ValidationException.class,
                () -> itemService.patchItem(ownerId, patchItemDto, 1L));
    }

    @Test
    void getByIdAndUserId_whenItemFoundAndNotOwner_thenReturnedItemWithoutBookings() {
        Long userId = 0L;
        Long itemId = 1L;
        Item foundedItem = getTestItem();
        foundedItem.setId(1L);
        User owner = getTestUser();
        foundedItem.setUser(owner);
        ItemDto expectedItemDto = getItemDto();
        expectedItemDto.setId(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(foundedItem));
        when(itemMapper.toItemDto(foundedItem)).thenReturn(expectedItemDto);
        setupComments();

        ItemDto actualItemDto = itemService.getByIdAndUserId(userId, itemId);

        assertEquals(expectedItemDto, actualItemDto);
        assertNull(actualItemDto.getLastBooking());
        assertNull(actualItemDto.getNextBooking());
    }

    @Test
    void getByIdAndUserId_whenItemFoundAndOwner_thenReturnedItemWithoutBookings() {
        Long userId = 1L;
        Long itemId = 1L;
        Item foundedItem = getTestItem();
        foundedItem.setId(1L);
        User owner = getTestUser();
        foundedItem.setUser(owner);
        ItemDto expectedItemDto = getItemDto();
        expectedItemDto.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(foundedItem));
        when(itemMapper.toItemDto(foundedItem)).thenReturn(expectedItemDto);
        setupComments();
        setupBookings();

        ItemDto actualItemDto = itemService.getByIdAndUserId(userId, itemId);

        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(1L, actualItemDto.getLastBooking().getId());
        assertEquals(2L, actualItemDto.getNextBooking().getId());
    }

    @Test
    void getByIdAndUserId_whenItemNotFoundAndOwner_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long itemId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(getTestUser()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getByIdAndUserId(userId, itemId));
    }

    @Test
    void getItemsByOwner_withoutBookingsAndComments_thenReturnListOfItemDtos() {
        Long ownerId = 1L;
        Item item = getTestItem();
        item.setId(1L);
        ItemDto itemDto = getItemDto();
        itemDto.setId(1L);
        List<Item> items = List.of(item);
        List<ItemDto> expectedItemDtos = List.of(itemDto);
        when(itemRepository.findAllByUserIdOrderByIdAsc(ownerId)).thenReturn(items);
        when(itemMapper.toItemDtoList(items)).thenReturn(expectedItemDtos);

        List<ItemDto> actualItemDtos = itemService.getItemsByOwner(ownerId);

        assertEquals(expectedItemDtos, actualItemDtos);
    }

    @Test
    void getItemsByOwner_withBookingsAndComments_thenReturnListOfItemDtos() {
        Long ownerId = 1L;
        Item item = getTestItem();
        item.setId(1L);
        ItemDto itemDto = getItemDto();
        itemDto.setId(1L);
        List<Item> items = List.of(item);
        List<ItemDto> expectedItemDtos = List.of(itemDto);
        Comment comment = getTestComment();
        comment.setItem(item);
        comment.setId(1L);
        CommentDto commentDto = getCommentDto();
        commentDto.setId(1L);
        List<CommentDto> commentDtos = List.of(commentDto);
        expectedItemDtos.get(0).setComments(commentDtos);

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item);
        lastBooking.setStatus(StatusBooking.APPROVED);
        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setItem(item);
        nextBooking.setStatus(StatusBooking.APPROVED);
        BookingInItemDto lastBookingInItemDto = new BookingInItemDto();
        lastBookingInItemDto.setId(1L);
        BookingInItemDto nextBookingInItemDto = new BookingInItemDto();
        nextBookingInItemDto.setId(2L);
        expectedItemDtos.get(0).setLastBooking(lastBookingInItemDto);
        expectedItemDtos.get(0).setNextBooking(nextBookingInItemDto);
        when(itemRepository.findAllByUserIdOrderByIdAsc(ownerId)).thenReturn(items);
        when(itemMapper.toItemDtoList(items)).thenReturn(expectedItemDtos);
        when(commentRepository.findByItemIn(eq(items), any(Sort.class))).thenReturn(List.of(comment));
        when(commentMapper.toDtoList(anyList())).thenReturn(commentDtos);
        when(bookingRepository.findByItemInAndStartLessThanEqual(
                eq(items), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findByItemInAndStartAfter(eq(items), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(nextBooking));
        when(bookingMapper.toBookingInItemDto(lastBooking)).thenReturn(lastBookingInItemDto);
        when(bookingMapper.toBookingInItemDto(nextBooking)).thenReturn(nextBookingInItemDto);

        List<ItemDto> actualItemDtos = itemService.getItemsByOwner(ownerId);

        assertEquals(expectedItemDtos, actualItemDtos);
    }

    @Test
    void searchItemsByText_whenItemsFound_thenReturnListOfItemDtos() {
        List<Item> foundedItems = List.of(getTestItem());
        List<ItemDto> expectedItemDtos = List.of(getItemDto());
        String text = "Test";
        when(itemRepository.search(text)).thenReturn(foundedItems);
        when(itemMapper.toItemDtoList(foundedItems)).thenReturn(expectedItemDtos);

        List<ItemDto> actualItemDtos = itemService.searchItemsByText(1L, text);

        assertEquals(expectedItemDtos, actualItemDtos);
    }

    @Test
    void searchItemsByText_whenSearchTextIsBlank_thenReturnEmptyList() {
        String text = "";

        List<ItemDto> actualItemDtos = itemService.searchItemsByText(1L, text);

        assertEquals(0, actualItemDtos.size());
    }


    @Test
    void addNewComment_whenInvoked_thenSavedComment() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDtoToSave = getCommentDto();
        Comment commentToSave = getTestComment();
        User expectedAuthor = getTestUser();
        Item expectedItem = getTestItem();
        expectedItem.setId(itemId);
        expectedAuthor.setId(userId);
        Booking booking = new Booking();
        booking.setBooker(expectedAuthor);
        booking.setItem(expectedItem);
        booking.setStatus(StatusBooking.APPROVED);
        List<Booking> bookingList = Collections.singletonList(booking);
        CommentDto expectedCommentDto = getCommentDto();
        expectedCommentDto.setAuthorName(expectedAuthor.getName());
        when(bookingRepository.findByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(StatusBooking.class), any(LocalDateTime.class))).thenReturn(bookingList);
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedAuthor));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(commentMapper.toComment(commentDtoToSave)).thenReturn(commentToSave);
        when(commentRepository.save(commentToSave)).thenReturn(commentToSave);
        when(commentMapper.toCommentDto(commentToSave)).thenReturn(expectedCommentDto);

        CommentDto actualCommentDto = itemService.addNewComment(userId, commentDtoToSave, itemId);

        assertEquals(expectedCommentDto, actualCommentDto);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();
        assertEquals(expectedAuthor, savedComment.getAuthor());
        assertEquals(expectedItem, savedComment.getItem());
    }

    @Test
    void addNewComment_withoutFinishedBookings_thenValidationExceptionThrown() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = getCommentDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(getTestUser()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(getTestItem()));
        when(bookingRepository.findByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(StatusBooking.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> itemService.addNewComment(userId, commentDto, itemId));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addNewComment_whenAuthorNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 1L;
        Long userId = 0L;
        CommentDto commentDto = getCommentDto();
        Comment comment = getTestComment();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(userId, commentDto, itemId));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addNewComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 1L;
        Long userId = 0L;
        CommentDto commentDto = getCommentDto();
        Comment comment = getTestComment();
        when(userRepository.findById(userId)).thenReturn(Optional.of(getTestUser()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(userId, commentDto, itemId));
        verify(commentRepository, never()).save(Mockito.any());
    }

    User getTestUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("TestName");
        newUser.setEmail("TestEmail");
        return newUser;
    }

    Item getTestItem() {
        return Item.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDtoReqCreate getCreateItemDto() {
        return ItemDtoReqCreate.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    Request getTestRequest() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("TestDescription");
        request.setCreated(LocalDateTime.now());
        return request;
    }

    ItemDtoReqPatch getPatchItem() {
        return ItemDtoReqPatch.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getUpdatedItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        return commentDto;
    }

    Comment getTestComment() {
        Comment comment = new Comment();
        comment.setText("Test");
        return comment;
    }

    void setupBookings() {
        Booking lastBooking = new Booking();
        Booking nextBooking = new Booking();
        lastBooking.setStatus(StatusBooking.APPROVED);
        nextBooking.setStatus(StatusBooking.APPROVED);
        BookingInItemDto lastBookingInItemDto = new BookingInItemDto();
        lastBookingInItemDto.setId(1L);
        BookingInItemDto nextBookingInItemDto = new BookingInItemDto();
        nextBookingInItemDto.setId(2L);
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqual(
                anyLong(), any(LocalDateTime.class), any(Sort.class))).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndStartAfter(
                anyLong(), any(LocalDateTime.class), any(Sort.class))).thenReturn(nextBooking);
        when(bookingMapper.toBookingInItemDto(lastBooking)).thenReturn(lastBookingInItemDto);
        when(bookingMapper.toBookingInItemDto(nextBooking)).thenReturn(nextBookingInItemDto);
    }

    void setupComments() {
        List<Comment> commentList = Collections.emptyList();
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(commentList);
        when(commentMapper.toDtoList(commentList)).thenReturn(Collections.emptyList());
    }

}