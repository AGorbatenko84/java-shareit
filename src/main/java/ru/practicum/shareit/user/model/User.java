package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    @Email(message = "Это не почта")
    @NotBlank(message = "Почта не может быть пустой")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "В логине не могут быть пробелы.")
    private String name;

    private Set<Long> itemsId = new HashSet<>();

    public void addIdItem(Long itemId) {
        itemsId.add(itemId);
    }

}
