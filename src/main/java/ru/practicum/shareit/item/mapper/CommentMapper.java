package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto){
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setId(commentDto.getId());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        return commentDto;
    }
}
