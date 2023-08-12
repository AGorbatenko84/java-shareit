package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoReqCreate {
    @Length(max = 50, message = "Максимальная длина описания — 50 символов")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Статус должен быть")
    private Boolean available;

    private Long requestId;
}
