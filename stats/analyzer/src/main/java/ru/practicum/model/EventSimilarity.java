package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "similarities", uniqueConstraints = {@UniqueConstraint(name = "UniqueEventId1AndEventId2", columnNames = {"eventId1", "eventId2"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_id1", nullable = false)
    Long eventId1;

    @Column(name = "event_id2", nullable = false)
    Long eventId2;

    @Column(name = "similarity", nullable = false)
    Double similarity;

    @Column(name = "ts", nullable = false)
    Instant timestamp;
}
