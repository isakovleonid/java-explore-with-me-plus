
package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.in.*;
import ru.practicum.comments.dto.output.CommentFullDto;
import ru.practicum.comments.dto.output.CommentShortDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.StateFilter;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ForbiddenException;
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

    public CommentShortDto create(NewCommentDto newCommentDto, Long userId, Long eventId) {
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

        return commentMapper.toCommentShortDto(comment);
    }

    public void delete(CommentParam param) {
        Comment comment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());

        if (!comment.getAuthor().getId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + comment.getId());
        }

        commentRepository.deleteById(param.getCommentId());
        log.info("Comment {} was deleted", comment);
    }

    public void delete(Long commentId) {
        checkIfExist(commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id = {} was deleted by admin", commentId);
    }

    public CommentFullDto update(NewCommentDto newComment, CommentParam param) {
        Comment existingComment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());
        if (!existingComment.getAuthor().getId().equals(param.getUserId())) {
            throw new ForbiddenException("User with id " + param.getUserId() + " is not author of comment " + existingComment.getId());
        }

        if (existingComment.getState() == State.PUBLISHED) {
            existingComment.setState(State.PENDING);
        } else if (existingComment.getState() == State.CANCELED) {
            throw new ConflictException("Cannot update canceled comment with id " + existingComment.getState().name());
        }

        existingComment.setText(newComment.getText());

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment was updated with id={}, old name='{}', new name='{}'",
                param.getCommentId(), existingComment.getText(), newComment.getText());
        return commentMapper.toCommentDto(updatedComment);
    }

    public CommentFullDto update(Long commentId, String filter) {

        State stateForUpdating = toState(filter);
        Comment existingComment = checkIfExist(commentId);

        if (existingComment.getState() != State.PENDING) {
            throw new ConflictException("Cannot update comment with state not PENDING");
        }

        existingComment.setState(stateForUpdating);

        Comment updatedComment = commentRepository.save(existingComment);
        log.info("Comment with id={} was updated with status {}", commentId, stateForUpdating);
        return commentMapper.toCommentDto(updatedComment);
    }

    public CommentFullDto getComment(CommentParam param) {
        Comment comment = checkIfExist(param.getUserId(), param.getEventId(), param.getCommentId());

        if (!comment.getAuthor().getId().equals(param.getUserId()) && comment.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Cannot get comment with id " + comment.getState().name());
        }

        if (!comment.getAuthor().getId().equals(param.getUserId())) {
            comment.setPublishedOn(null);
            comment.setState(null);
        }
        return commentMapper.toCommentDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(Long eventId, GetCommentParam param) {
        Long userId = param.getUserId();
        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;
        if (size == 0) {
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndAuthorIdAndState(userId, eventId, State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByEventId(CommentPublicParam param) {
        checkEventIfExist(param.getEventId());

        Integer from = param.getFrom();
        Integer size = param.getSize();
        List<Comment> comments;

        if (size == 0) {
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED).stream()
                    .skip(from)
                    .toList();
        } else if (from < size && size > 0) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            comments = commentRepository.findByEventIdAndState(param.getEventId(), State.PUBLISHED, pageRequest);
        } else {
            return List.of();
        }
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(GetCommentParam param) {
        userRepository.findById(param.getUserId())
                .orElseThrow(() -> new NotFoundException("user with id " + param.getUserId() + " not found"));

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getComments(CommentAdminParam param) {

        dateValidation(param);

        List<Comment> comments;
        if (param.getSize() == 0) {
            comments = getCommentsWithoutPagination(param);
        } else if (param.getFrom() < param.getSize() && param.getSize() > 0) {
            comments = getCommentsWithPagination(param);
        } else {
            return List.of();
        }

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    private List<Comment> getCommentsWithoutPagination(GetCommentParam param) {
        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId())
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()));

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(GetCommentParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByAuthorId(param.getUserId(), pageRequest)
                : commentRepository.findByAuthorIdAndState(param.getUserId(), toState(param.getStatus()), pageRequest);
    }

    private List<Comment> getCommentsWithoutPagination(CommentAdminParam param) {

        List<Comment> result = param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd())
                : commentRepository.findByStateAndCreatedOnBetween(
                        toState(param.getStatus()), param.getStart(), param.getEnd());

        return result.stream()
                .skip(param.getFrom())
                .toList();
    }

    private List<Comment> getCommentsWithPagination(CommentAdminParam param) {
        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        return param.getStatus() == StateFilter.ALL
                ? commentRepository.findByCreatedOnBetween(param.getStart(), param.getEnd(), pageRequest)
                : commentRepository.findByStateAndCreatedOnBetween(
                        toState(param.getStatus()), param.getStart(), param.getEnd(), pageRequest);
    }

    private State toState(StateFilter filter) {
        return switch (filter) {
            case PENDING -> State.PENDING;
            case PUBLISHED -> State.PUBLISHED;
            case CANCELED -> State.CANCELED;
            case ALL -> throw new IllegalArgumentException("ALL is not a valid State");
        };
    }

    private State toState(String filter) {
        return switch (filter) {
            case "APPROVE" -> State.PUBLISHED;
            case "REJECT" -> State.CANCELED;
            default -> throw new IllegalArgumentException(
                    "Parameter action must be APPROVE or REJECT, but action = " + filter);
        };
    }

    private Comment checkIfExist(Long userId, Long eventId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id " + userId + " not found"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id " + eventId + " not found"));
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with id " + commentId + " not found"));
    }

    private Comment checkIfExist(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment with id " + commentId + " not found"));
    }

    private void checkEventIfExist(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id " + eventId + " not found"));
    }

    private static void dateValidation(CommentAdminParam param) {
        if (param.getStart().isAfter(param.getEnd())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}
