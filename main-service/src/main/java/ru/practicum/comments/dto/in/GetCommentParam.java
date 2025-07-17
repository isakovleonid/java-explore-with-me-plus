package ru.practicum.comments.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetCommentParam {
    Long userId;
    Integer from;
    Integer to;
}