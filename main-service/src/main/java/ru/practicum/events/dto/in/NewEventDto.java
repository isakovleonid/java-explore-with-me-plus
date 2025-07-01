package ru.practicum.events.dto.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotEmpty
    @Length(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotEmpty
    @Length(min = 20, max = 7000)
    String description;
    @NotNull
    LocalDateTime eventDate;
    @NotNull
    Location location;
    @NotNull
    Boolean paid = false;
    @NotNull
    Integer participantLimit = 0;
    @NotNull
    Boolean requestModeration = true;
    @NotEmpty
    @Length(min = 3, max = 170)
    String title;
}
