package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventParticipationService {
    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    public void confirmAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));

        eventParticipation.setConfirmed(true);
        eventParticipation.setConfirmationTime(LocalDateTime.now());
        eventParticipationRepository.save(eventParticipation);
    }

    public void createAttendance(User user, Event event) {
        if (eventParticipationRepository.existsByUserAndEvent(user, event)) {
            throw new EntityExistsException("Event is already being attended.");
        }
        eventParticipationRepository.save(new EventParticipation(user, event));
    }

    public void deleteAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        eventParticipationRepository.delete(eventParticipation);
    }
}
