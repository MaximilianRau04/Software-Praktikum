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
    /**
     * Finds all upcoming events where the associated exchange day start date is greater than or equal to the given current date.
     *
     * @param currentDate the current date to compare with the event's exchange day's start date.
     * @return a list of events that are upcoming (i.e., the exchange day's start date is on or after the current date).
     */
    @Query("SELECT e FROM Event e JOIN FETCH e.exchangeDay d LEFT JOIN FETCH e.registeredUsers WHERE d.startDate >= :currentDate")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);

    /**
     * Finds all events that are associated with the specified tags.
     *
     * @param tags a list of {@link Tag} entities to filter the events by.
     * @return a list of events that have at least one of the specified tags.
     */
    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t IN :tags")
    List<Event> findEventsByTags(@Param("tags") List<Tag> tags);

    /**
     * Finds all events organized by the specified organizer.
     *
     * @param organizerId the ID of the organizer whose events are to be retrieved.
     * @return a list of events organized by the given organizer.
     */
    @Query("SELECT e FROM Event e JOIN e.organizer o WHERE o.id = :organizerId")
    List<Event> findAllEventsOfOrganizer(@Param("organizerId") Long organizerId);
}
