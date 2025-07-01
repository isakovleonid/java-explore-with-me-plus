package ru.practicum.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private List<String> errors;
    private String reason;
    private String message;
    HttpStatus status;
    Timestamp timestamp;

}
