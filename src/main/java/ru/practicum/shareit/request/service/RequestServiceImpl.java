package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemMapper itemMapper;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto addNewRequest(Long userId, RequestDtoCreate requestDtoCreate) {
        User requester = validateUserId(userId);
        Request request = requestMapper.toRequest(requestDtoCreate);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        requestRepository.save(request);
        RequestDto requestDto = requestMapper.toRequestDto(request);


        return requestDto;
    }

    @Override
    public List<RequestDto> getRequests(Long userId) {
        User requester = validateUserId(userId);
        List<Request> listRequests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<RequestDto> requestDtoList = requestMapper.toRequestDtoList(listRequests);
        setItemsToRequestDto(listRequests, requestDtoList);

        return requestDtoList;
    }

    private void setItemsToRequestDto(List<Request> requests, List<RequestDto> dtos) {
        Map<Long, List<Item>> items = itemRepository.findByRequestIn(requests)
                .stream()
                .collect(groupingBy(item -> item.getRequest().getId(), toList()));
        for (RequestDto dto : dtos) {
            List<Item> itemList = items.getOrDefault(dto.getId(), Collections.emptyList());
            dto.setItems(itemMapper.toItemDtoList(itemList));
        }
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        User requester = validateUserId(userId);
        PageRequest pageRequest = PageRequest.of((from / size), size, Sort.by("created").descending());
        Page<Request> page = requestRepository.findByRequesterIdIsNot(userId, pageRequest);
        List<Request> requests = page.getContent();
        List<RequestDto> requestDtoList = requestMapper.toRequestDtoList(requests);
        setItemsToRequestDto(requests, requestDtoList);

        return requestDtoList;
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        User requester = validateUserId(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException("Запрос с таким id отсутствует");
        });
        RequestDto requestDto = requestMapper.toRequestDto(request);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        requestDto.setItems(itemMapper.toItemDtoList(items));
        return requestDto;
    }

    private User validateUserId(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с таким id отсутствует");
        });
        return user;
    }
}
