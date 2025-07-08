package ru.practicum.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @Column
    private LocalDateTime createdOn;
    @Column
    private String description;
    @Column
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    @Column
    private Boolean paid = false;
    @Column
    private Integer participantLimit = 0;
    @Column
    private LocalDateTime publishedOn;
    @Column
    private Boolean requestModeration = true;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;
    @Column
    private String title;
}
