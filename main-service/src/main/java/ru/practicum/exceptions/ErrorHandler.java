package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static ru.practicum.constants.Fields.FORMATTER;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IncorrectlyMadeRequestException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectlyRequest(IncorrectlyMadeRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "Parameters invalid",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleOperationNotAllowed(OperationNotAllowedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "For the requested operation the conditions are not met",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoHavePermissionException.class)
    public ResponseEntity<ErrorResponse> handleNoHavePermission(NoHavePermissionException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                List.of(Arrays.toString(e.getStackTrace())),
                "This user no have permission to access this object",
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DateException.class)
    public ResponseEntity<ErrorResponse> handleDateException(DateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                LocalDateTime.now().format(FORMATTER)
        );
        log.error("DateException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "The required object was not found.",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Not found error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);

        String message = String.format("Field: %s. Error: %s. Value: %s",
                firstError.getField(),
                firstError.getDefaultMessage(),
                firstError.getRejectedValue());

        ErrorResponse response = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Method Argument Not Valid: {}", message);
        return ResponseEntity.badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException ex) {
        ErrorResponse response = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Duplicate error: {}", ex.getMessage());
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
                List.of(Arrays.toString(ex.getStackTrace())),
                "Incorrectly made request.",
                message,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Method Argument Type Mismatch: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        ErrorResponse response = new ErrorResponse(
                List.of(Arrays.toString(ex.getStackTrace())),
                "For the requested operation the conditions are not met.",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(FORMATTER)
        );

        log.error("Category is not empty: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}