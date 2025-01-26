package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link EventParticipation} entities.
 *
 * <p>Provides CRUD operations and custom queries for managing the relationship between users
 * and events. Extends {@link JpaRepository} to leverage standard Spring Data JPA functionality.</p>
 */
@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    /**
     * Finds an {@link EventParticipation} entity by the specified user and event.
     *
     * <p>This method retrieves the participation record for a given combination of user and event
     * if it exists. It is typically used to check or fetch details about a user's participation in
     * a specific event.</p>
     *
     * @param user the user whose participation is being queried.
     * @param event the event associated with the participation.
     * @return an {@link Optional} containing the {@link EventParticipation} if found, or an empty Optional if not.
     */
    Optional<EventParticipation> findByUserAndEvent(User user, Event event);

    /**
     * Checks whether a participation record exists for the specified user and event.
     *
     * <p>This method is used to verify whether a given user is already associated with an event
     * (e.g., to prevent duplicate participation entries or validate preconditions).</p>
     *
     * @param user the user whose participation is being checked.
     * @param event the event to check for the user's participation.
     * @return {@code true} if a participation record exists for the user and event, otherwise {@code false}.
     */
    boolean existsByUserAndEvent(User user, Event event);
}
