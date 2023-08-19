package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoReqPatch {
    @NotBlank
    private Long id;
    @Length(max = 50, message = "Максимальная длина описания — 50 символов")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    private Boolean available;
}
