package ru.practicum.handling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.util.Constants.PATTERN_FORMATE_DATE;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingController {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern(PATTERN_FORMATE_DATE);

    private ApiError api(HttpStatus status, String reason, String message, List<String> errors) {
        return new ApiError(
                errors == null ? List.of() : errors,
                message,
                reason,
                status.name(),
                LocalDateTime.now().format(FMT)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError onNotFoundException(NotFoundException e) {
        log.warn("404 {}", e.getMessage());

        return api(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getMessage(),
                null);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError onConflictException(ConflictException e) {
        log.warn("409 {}", e.getMessage());

        return api(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                null);
    }
}
