package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @Length(max = 50, message = "Максимальная длина описания — 50 символов")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotBlank
    private boolean isAvailable;
    @NotBlank(message = "Предметом точно кто-то владеет")
    private Long userId;

    private ItemRequest request;

}
