package ru.practicum.compilation.dto.in;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public class NewCompilationDto {
    @UniqueElements
    List<Long> events;
    Boolean pinned = false;
    @NotEmpty
    @Length(min = 1, max = 50)
    String title;
}
