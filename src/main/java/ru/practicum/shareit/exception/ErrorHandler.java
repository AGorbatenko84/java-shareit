package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Map;


@RestControllerAdvice//("ru.practicum.shareit.exception")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("error", "Валидация не пройдена.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        return Map.of("error", "Объект не найден.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        return Map.of("error", "Объект не может быть создан.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleError(final Throwable e) {
        return Map.of("error", "Произошла ошибка!");
    }
}
