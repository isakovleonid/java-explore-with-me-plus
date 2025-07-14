package ru.practicum.events.storage;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventParam;
import ru.practicum.events.model.QEvent;

import java.util.List;

@Repository
public class EventQueryDslRepositoryImpl implements EventQueryDslRepository {
    private final QEvent event = QEvent.event;
    private final JPAQueryFactory queryFactory;

    public EventQueryDslRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Event> findEventsByParam(EventParam param, Pageable pageable) {

        BooleanBuilder predicate = new BooleanBuilder();
        if (param.getUsers() != null && !param.getUsers().isEmpty()) {
            predicate.and(event.initiator.id.in(param.getUsers()));
        }
        if (param.getStates() != null && !param.getStates().isEmpty()) {
            predicate.and(event.state.in(param.getStates()));
        }
        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            predicate.and(event.category.id.in(param.getCategories()));
        }
        if (param.getStart() != null) {
            predicate.and(event.eventDate.after(param.getStart()));
        }
        if (param.getEnd() != null) {
            predicate.and(event.eventDate.before(param.getEnd()));
        }

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(event.eventDate.desc());
        return query.fetch();
    }
}
