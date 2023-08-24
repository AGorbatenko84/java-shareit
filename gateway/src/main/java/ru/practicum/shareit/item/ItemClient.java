package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoReqCreate;
import ru.practicum.shareit.item.dto.ItemDtoReqPatch;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long ownerId, ItemDtoReqCreate dto) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> patch(Long ownerId, ItemDtoReqPatch dto, Long id) {
        return patch("/" + id, ownerId, dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> delete(Long userId, Long id) {
        return delete("/" + id, userId);
    }

    public ResponseEntity<Object> getByOwnerId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long userId, Long id, CommentDto commentDto) {
        return post("/" + id + "/comment", userId, commentDto);
    }
}