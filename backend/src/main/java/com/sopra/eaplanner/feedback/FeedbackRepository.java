package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Feedback} entities.
 * Provides methods for querying and persisting feedback data in the database.
 * Extends {@link CrudRepository} to inherit basic CRUD operations such as save, delete, and find.
 *
 * <p>Additional query methods are defined for retrieving feedback based on event and trainer profile.</p>
 *
 * <p>All repository methods return results wrapped in {@link Optional} or {@link List}, depending on the expected outcome.</p>
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Retrieves all feedbacks associated with a specific event.
     *
     * @param eventId the ID of the event to filter the feedbacks by
     * @return a {@link List} of {@link Feedback} objects related to the given event ID
     */
    List<Feedback> findByEventId(Long eventId);

    /**
     * Fetches all feedback with the passed user object that has been given through the lifecycle of the project
     * @param organizer The user whose feedback is fetched
     * @return A List of all Feedback that the user has received prior
     */
    @Query("SELECT f FROM Feedback f WHERE f.event.organizer = :organizer")
    List<Feedback> findByEventOrganizer(@Param("organizer") User organizer);

}
