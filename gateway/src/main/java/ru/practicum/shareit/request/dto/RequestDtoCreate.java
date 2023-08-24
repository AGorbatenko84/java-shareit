package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestDtoCreate {
    @Length(max = 1000, message = "Максимальная длина описания — 1000 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
