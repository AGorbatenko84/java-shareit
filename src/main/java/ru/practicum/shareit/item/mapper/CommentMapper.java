package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;


@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface CommentMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "item", ignore = true)
    Comment toComment(CommentDto commentDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toDtoList(List<Comment> comments);
}
