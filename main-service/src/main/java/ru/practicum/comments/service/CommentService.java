package ru.practicum.comments.service;

import ru.practicum.comments.dto.in.CommentParam;
import ru.practicum.comments.dto.in.GetCommentParam;
import ru.practicum.comments.dto.in.NewCommentDto;
import ru.practicum.comments.dto.output.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(NewCommentDto newCommentDto, Long userId, Long eventId);

    void delete(CommentParam param);

    CommentDto update(NewCommentDto newComment, CommentParam param);

    CommentDto getComment(CommentParam param);

    List<CommentDto> getCommentsByEventId(Long eventId, GetCommentParam param);

    List<CommentDto> getComments(GetCommentParam param);
}