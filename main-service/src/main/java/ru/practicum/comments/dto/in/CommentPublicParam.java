package ru.practicum.comments.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPublicParam {
    Long eventId;
    Integer from = 0;
    Integer size = 10;
}

