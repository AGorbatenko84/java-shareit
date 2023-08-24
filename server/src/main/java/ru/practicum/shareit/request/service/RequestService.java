package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoCreate;

import java.util.List;

public interface RequestService {
    RequestDto addNewRequest(Long userId, RequestDtoCreate requestDto);

    List<RequestDto> getRequests(Long userId);

    List<RequestDto> getAllRequests(Long userId, Integer from, Integer size);

    RequestDto getRequestById(Long userId, Long requestId);
}
