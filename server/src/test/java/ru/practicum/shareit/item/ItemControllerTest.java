package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoReqCreate;
import ru.practicum.shareit.item.dto.ItemDtoReqPatch;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void add_whenInvoked_thenStatusIsCreatedAndReturnedItemDto() {
        ItemDtoReqCreate createItemDto = getCreateItemDto();
        long ownerId = 1L;
        ItemDto savedDto = getItemDto();
        when(itemService.addNewItem(ownerId, createItemDto)).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDto))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(savedDto)));
    }

    @SneakyThrows
    @Test
    void patchItem_whenItemFound_thenReturnUpdatedItemDto() {
        ItemDtoReqPatch patchItemDto = getPatchItemDto();
        ItemDto updatedItemDto = getUpdatedItemDto();
        long itemId = 1L;
        long ownerId = 1L;
        when(itemService.patchItem(ownerId, patchItemDto, itemId)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedItemDto)));
    }

    @SneakyThrows
    @Test
    void getById_whenItemFound_thenReturnedItemDto() {
        long itemId = 1L;
        long userId = 1L;
        ItemDto dto = getItemDto();
        dto.setId(1L);
        when(itemService.getByIdAndUserId(userId, itemId)).thenReturn(dto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @Test
    void getByOwner() {
        long ownerId = 1L;
        List<ItemDto> dtoList = List.of(getItemDto());
        when(itemService.getItemsByOwner(ownerId)).thenReturn(dtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void getByDescription_whenItemsFound_thenReturnedListOfItemDtos() {
        long userId = 1L;
        String text = "Test";
        List<ItemDto> dtoList = List.of(getItemDto());
        when(itemService.searchItemsByText(userId, text)).thenReturn(dtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)));
    }

    @SneakyThrows
    @Test
    void addComment_whenInvoked_thenReturnedSavedCommentDto() {
        long itemId = 1L;
        long userId = 1L;
        CommentDto dto = getCommentDto();
        when(itemService.addNewComment(userId, dto, itemId)).thenReturn(dto);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    ItemDtoReqCreate getCreateItemDto() {
        return ItemDtoReqCreate.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getItemDto() {
        return ItemDto.builder()
                .name("TestName")
                .description("TestDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDtoReqPatch getPatchItemDto() {
        return ItemDtoReqPatch.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    ItemDto getUpdatedItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(Boolean.TRUE)
                .build();
    }

    CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test");
        return commentDto;
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}