package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    Optional<EventParticipation> findByUserAndEvent(User user, Event event);
}
