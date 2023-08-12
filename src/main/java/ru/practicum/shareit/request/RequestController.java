package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoCreate;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody RequestDtoCreate requestDto) {
        return requestService.addNewRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getBookingsOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0", required = false) @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10", required = false) @Min(1) @Max(100) Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
