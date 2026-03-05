package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    Optional<EventSimilarity> findByEventId1AndEventId2(Long eventId1, Long eventId2);

    @Query("SELECT es FROM EventSimilarity es WHERE es.eventId1 = :eventId OR es.eventId2 = :eventId")
    List<EventSimilarity> findAllByEventId(@Param("eventId") long eventId);

    @Query("SELECT es FROM EventSimilarity es " +
            "WHERE (es.eventId1 IN :eventIds OR es.eventId2 IN :eventIds) " +
            "ORDER BY es.similarity DESC")
    List<EventSimilarity> findSimilarEventsForListEventIds(@Param("eventIds") List<Long> eventIds);
}
