package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Event;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    private Long id;
    @ManyToMany
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private List<Event> events;
    @Column
    private Boolean pinned;
    @Column
    private String title;
}
