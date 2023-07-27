package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @Pattern(regexp = "^[a-z]([a-zA-Z0-9]*)?$")
    private String name;
    private String description;
    private Boolean available;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;

}
