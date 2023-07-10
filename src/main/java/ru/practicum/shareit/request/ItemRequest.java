package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;

    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotBlank(message = "Юзер создавший запрос должен быть")
    private Long userId;

    @FutureOrPresent(message = "Запрос нельзя создать в прошлом")
    private LocalDate createdDate;

}
