package ru.practicum.events.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
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
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
