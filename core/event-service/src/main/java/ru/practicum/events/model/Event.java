package ru.practicum.events.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.enums.event.EventState;
import ru.practicum.validation.FieldDescription;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @FieldDescription(value = "Уникальный идентификатор собятия", changeByCopy = false)
    private Integer id;

    @Column(nullable = false, length = 2000)
    @FieldDescription(value = "Аннотация")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @FieldDescription(value = "Категория события")
    private Category category;

    @Builder.Default
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Builder.Default
    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(nullable = false, length = 7000)
    @FieldDescription(value = "Описание")
    private String description;

    @Column(name = "event_date", nullable = false)
    @FieldDescription(value = "Дата проведения")
    private LocalDateTime eventDate;

    @Column(name = "initiator_id", nullable = false)
    @FieldDescription(value = "Пользователь")
    private Integer initiatorId;

    @Column(name = "location_lat")
    @FieldDescription(value = "Д")
    private Float locationLat;

    @Column(name = "location_lon")
    @FieldDescription(value = "Ш")
    private Float locationLon;

    @Builder.Default
    @Column(nullable = false)
    @FieldDescription(value = "Утверждение")
    private Boolean paid = false;

    @Builder.Default
    @Column(name = "participant_limit")
    @FieldDescription(value = "Ограничение по колву")
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Builder.Default
    @Column(name = "request_moderation")
    @FieldDescription(value = "Модерация")
    private Boolean requestModeration = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @FieldDescription(value = "Состояние")
    private EventState state = EventState.PENDING;

    @Column(nullable = false, length = 120)
    @FieldDescription(value = "Заголовок")
    private String title;

    @Builder.Default
    private double rating = 0.0;
}
