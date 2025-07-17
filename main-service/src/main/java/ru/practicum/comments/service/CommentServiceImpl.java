
package ru.practicum.comments.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.in.CommentParam;
import ru.practicum.comments.dto.in.GetCommentParam;
import ru.practicum.comments.dto.in.NewCommentDto;
import ru.practicum.comments.dto.output.CommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentDto create(NewCommentDto newCommentDto, Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id " + eventId + " not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Cannot add comment because the event it's not status published : "
                    + event.getState().name());
        }

        Comment comment = commentMapper.toComment(newCommentDto, event, user);
        if (comment.getState() == null) {
            comment.setState(State.PENDING);
        }
        comment = commentRepository.save(comment);

        return commentMapper.toCommentDto(comment);
    }

    public void delete(CommentParam param) {
        Comment comment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());

        if (!comment.getAuthor().getId().equals(param.getUserId())) {
            throw new ValidationException("User with id " + param.getUserId() + " is not author of comment " + comment.getId());
        }

        commentRepository.deleteById(param.getCommentId());
        log.info("Comment {} was deleted", comment);
    }

    public CommentDto update(NewCommentDto newComment, CommentParam param) {
        Comment existingComment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());
        if (!existingComment.getAuthor().getId().equals(param.getUserId())) {
            throw new ValidationException("User with id " + param.getUserId() + " is not author of comment " + existingComment.getId());
        }

        if (existingComment.getState() != State.PENDING) {
            throw new ConflictException("Cannot publish the comment because it's not in the right state: "
                    + existingComment.getState().name());
        }

        existingComment.setText(newComment.getText());

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment was updated with id={}, old name='{}', new name='{}'",
                param.getCommentId(), existingComment.getText(), newComment.getText());
        return commentMapper.toCommentDto(updatedComment);
    }

    public CommentDto getComment(CommentParam param) {
        Comment comment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());
        return commentMapper.toCommentDto(comment);
    }

    public List<CommentDto> getCommentsByEventId(Long eventId, GetCommentParam param) {
        Long userId = param.getUserId();
        Integer from = param.getFrom();
        Integer to = param.getTo();
        List<Comment> comments;
        if (to == 0) {
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < to && to > 0) {
            PageRequest pageRequest = PageRequest.of(from / to, to);
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    public List<CommentDto> getComments(GetCommentParam param) {
        Long userId = param.getUserId();
        Integer from = param.getFrom();
        Integer to = param.getTo();
        List<Comment> comments;
        if (to == 0) {
            comments = commentRepository.findByAuthorIdAndState(userId, State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < to && to > 0) {
            PageRequest pageRequest = PageRequest.of(from / to, to);
            comments = commentRepository.findByAuthorIdAndState(userId, State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    private Comment checkIfExist(Long userId, Long eventId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id " + eventId + " not found"));
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with id " + commentId + " not found"));
    }
}
