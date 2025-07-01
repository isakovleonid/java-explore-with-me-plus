package ru.practicum.events.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationCreateRequest {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}
