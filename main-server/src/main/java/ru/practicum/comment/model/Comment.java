package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinTable(name = "comments_events",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Event event;

    @ManyToOne
    @JoinTable(name = "comments_users",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user;
}