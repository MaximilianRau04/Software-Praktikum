package com.sopra.eaplanner.feedback;

import org.springframework.data.repository.CrudRepository;
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
public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

    /**
     * Retrieves all feedbacks associated with a specific event.
     *
     * @param eventId the ID of the event to filter the feedbacks by
     * @return a {@link List} of {@link Feedback} objects related to the given event ID
     */
    List<Feedback> findByEventId(Long eventId);

    /**
     * Retrieves the feedback associated with a specific trainer profile.
     *
     * @param trainerId the ID of the trainer profile to filter the feedback by
     * @return an {@link Optional} containing the {@link Feedback} for the given trainer profile ID, if it exists
     */
    Optional<Feedback> findByTrainerProfileId(Long trainerId);

}
