package ru.practicum.requests.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    LocalDateTime created;
    @NotNull
    Long event;
    Long id;
    Long requester;
    @NotNull
    Status status;
}
