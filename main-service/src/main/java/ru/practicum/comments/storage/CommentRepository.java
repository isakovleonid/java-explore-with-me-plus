package ru.practicum.comments.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.State;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventIdAndAuthorIdAndState(Long eventId, Long authorId, State state, Pageable pageable);

    List<Comment> findByEventIdAndAuthorIdAndState(Long eventId, Long authorId, State state);

    List<Comment> findByAuthorIdAndState(Long eventId, State state, Pageable pageable);

    List<Comment> findByAuthorIdAndState(Long eventId, State state);
}