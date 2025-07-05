package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                List.of(),
                "The required object was not found.",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now().format(formatter)
        );

        log.error("Not found error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.joining(", "));

        ErrorResponse response = new ErrorResponse(
                List.of(),
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(formatter)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DuplicateException ex) {
        ErrorResponse response = new ErrorResponse(
                List.of(),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(formatter)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Failed to convert value of type %s to required type %s",
                ex.getValue(),
                ex.getRequiredType().getSimpleName()
        );

        ErrorResponse response = new ErrorResponse(
                List.of(),
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(formatter)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}