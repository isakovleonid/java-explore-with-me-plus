package ru.practicum.comments.service;

import ru.practicum.comments.dto.in.CommentParam;
import ru.practicum.comments.dto.in.GetCommentParam;
import ru.practicum.comments.dto.in.NewCommentDto;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.dto.output.CommentShortDto;

import java.util.List;

public interface CommentService {
    CommentShortDto create(NewCommentDto newCommentDto, Long userId, Long eventId);

    void delete(CommentParam param);

    CommentFullDto update(NewCommentDto newComment, CommentParam param);

    CommentFullDto getComment(CommentParam param);

    List<CommentFullDto> getCommentsByEventId(Long eventId, GetCommentParam param);

    List<CommentFullDto> getComments(GetCommentParam param);
}