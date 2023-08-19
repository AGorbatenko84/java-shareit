package ru.practicum.shareit.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoCreate;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface RequestMapper {

    @Mapping(target = "items", ignore = true)
    RequestDto toRequestDto(Request request);

    List<RequestDto> toRequestDtoList(List<Request> requests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "created", ignore = true)
    Request toRequest(RequestDtoCreate requestDtoCreate);

}
