package ru.practicum.events.storage;

import org.springframework.data.domain.Pageable;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventParam;

import java.util.List;

public interface EventQueryDslRepository {
    List<Event> findEventsByParam(EventParam param, Pageable pageable);
}
