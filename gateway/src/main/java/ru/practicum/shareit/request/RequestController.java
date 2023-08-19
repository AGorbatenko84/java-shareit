package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoCreate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody RequestDtoCreate requestDto) {
        return requestClient.create(userId, requestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0", required = false) @Min(0) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object>  getBookingsOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getOwn(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object>  getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long requestId) {
        return requestClient.getById(userId, requestId);
    }
}
