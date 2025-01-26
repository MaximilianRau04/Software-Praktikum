package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    Optional<EventParticipation> findByUserAndEvent(User user, Event event);

    boolean existsByUserAndEvent(User user, Event event);

    /**
     * Finds all {@link EventParticipation} entries for a given user where feedback has been given.
     *
     * <p>This method retrieves all participation records for a user where feedbackGiven is true.</p>
     *
     * @param user the user whose participation records are being queried.
     * @return a list of {@link EventParticipation} where feedbackGiven is true.
     */
    List<EventParticipation> findByUserAndFeedbackGivenTrue(User user);

    /**
     * Counts the number of {@link EventParticipation} entries for a given user where feedback has been given.
     *
     * <p>This method counts how many events a user has participated in and provided feedback for.</p>
     *
     * @param user the user whose participation count is being queried.
     * @return the count of {@link EventParticipation} where feedbackGiven is true.
     */
    long countByUserAndFeedbackGivenTrue(User user);
}
