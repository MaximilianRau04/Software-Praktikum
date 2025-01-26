package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.tags.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    @Query("SELECT e FROM Event e JOIN FETCH e.exchangeDay d LEFT JOIN FETCH e.registeredUsers WHERE d.startDate >= :currentDate")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t IN :tags")
    List<Event> findEventsByTags(@Param("tags") List<Tag> tags);

    @Query("SELECT e FROM Event e JOIN e.organizer o WHERE o.id = :organizerId")
    List<Event> findAllEventsOfOrganizer(@Param("organizerId") Long organizerId);
}
