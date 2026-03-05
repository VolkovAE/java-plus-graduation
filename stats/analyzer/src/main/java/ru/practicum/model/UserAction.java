package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "interactions", uniqueConstraints = {@UniqueConstraint(name = "UniqueUserIdAndEventId", columnNames = {"userId", "eventId"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "rating", nullable = false)
    Double rating;

    @Column(name = "ts", nullable = false)
    Instant timestamp;
}
