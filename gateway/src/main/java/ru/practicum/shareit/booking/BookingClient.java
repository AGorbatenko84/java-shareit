package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDtoReqCreate;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, BookingDtoReqCreate dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> setApprove(long ownerId, long id, String approved) {
        UriComponents builder = UriComponentsBuilder.newInstance()
                .path("/" + id)
                .query("approved={approved}")
                .buildAndExpand(approved);
        return patch(builder.toUriString(), ownerId, null);
    }

    public ResponseEntity<Object> getById(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllForUserByState(long userId, String state, Integer from, Integer size) {
        UriComponents builder = UriComponentsBuilder.newInstance()
                .query("state={state}&from={from}&size={size}")
                .buildAndExpand(state, from, size);
        return get(builder.toUriString(), userId);
    }

    public ResponseEntity<Object> getAllForOwnerByState(long userId, String state, Integer from, Integer size) {
        UriComponents builder = UriComponentsBuilder.newInstance()
                .path("/owner")
                .query("state={state}&from={from}&size={size}")
                .buildAndExpand(state, from, size);
        return get(builder.toUriString(), userId);
    }
}
