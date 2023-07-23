package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @Length(max = 200, message = "Максимальная длина комментария — 200 символов")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
    private Long itemId;
}
