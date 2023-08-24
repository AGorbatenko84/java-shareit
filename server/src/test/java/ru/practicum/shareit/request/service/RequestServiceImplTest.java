package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoCreate;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void create_whenInvoked_thenSavedRequest() {
        Long userId = 1L;
        RequestDtoCreate dto = new RequestDtoCreate();
        dto.setDescription("TestDescription");
        User user = getUser();
        Request requestToSave = requestMapper.toRequest(dto);
        requestToSave.setRequester(user);
        requestToSave.setId(1L);
        RequestDto expectedRequestDto = requestMapper.toRequestDto(requestToSave);
        LocalDateTime createdTime = LocalDateTime.now();
        expectedRequestDto.setCreated(createdTime);
        doAnswer(invocationOnMock -> {
            Request request = invocationOnMock.getArgument(0, Request.class);
            request.setId(1L);
            return request;
        }).when(requestRepository).save(any(Request.class));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RequestDto actualRequestDto = requestService.addNewRequest(userId, dto);
        actualRequestDto.setCreated(createdTime);
        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        RequestDtoCreate dto = new RequestDtoCreate();
        dto.setDescription("TestDescription");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.addNewRequest(userId, dto));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestById_whenItemRequestFound_thenReturnItemRequestDto() {
        Long userId = 1L;
        User foundedUser = getUser();
        Long itemRequestId = 1L;
        Request foundedRequest = getRequest();
        RequestDto expectedDto = requestMapper.toRequestDto(foundedRequest);
        expectedDto.setItems(Collections.emptyList());
        when(userRepository.findById(userId)).thenReturn(Optional.of(foundedUser));
        when(requestRepository.findById(itemRequestId)).thenReturn(Optional.of(foundedRequest));
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(Collections.emptyList());

        RequestDto actualDto = requestService.getRequestById(itemRequestId, userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getRequestById_whenItemRequestNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long itemRequestId = 1L;
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.empty());
        lenient().when(requestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(itemRequestId, userId));
    }

    @Test
    void getRequests_whenInvoked_thenReturnListOfOwnItemRequestDtos() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(getUser()));
        List<Request> foundedRequests = List.of(getRequest());
        List<Item> items = List.of(getTestItem());
        List<RequestDto> expectedDtos = requestMapper.toRequestDtoList(foundedRequests);
        expectedDtos.get(0).setItems(itemMapper.toItemDtoList(items));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(foundedRequests);
        when(itemRepository.findByRequestIn(foundedRequests)).thenReturn(items);

        List<RequestDto> actualDtos = requestService.getRequests(userId);

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getAllRequests_whenInvoked_thenReturnListOfAllItemRequestDtos() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<Request> foundedItemRequests = List.of(getRequest());
        List<Item> items = List.of(getTestItem());
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<Request> page = new PageImpl<>(foundedItemRequests);
        List<RequestDto> expectedDtos = requestMapper.toRequestDtoList(foundedItemRequests);
        expectedDtos.get(0).setItems(itemMapper.toItemDtoList(items));
        when(userRepository.findById(userId)).thenReturn(Optional.of(getUser()));
        when(requestRepository.findByRequesterIdIsNot(userId, pageRequest)).thenReturn(page);
        when(itemRepository.findByRequestIn(foundedItemRequests)).thenReturn(items);

        List<RequestDto> actualDtos = requestService.getAllRequests(userId, from, size);

        assertEquals(expectedDtos, actualDtos);
    }

    User getUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    Request getRequest() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("TestDescription");
        request.setRequester(getUser());
        request.setCreated(LocalDateTime.now());
        return request;
    }

    Item getTestItem() {
        return Item.builder()
                .id(1L)
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .request(getRequest())
                .build();
    }
}