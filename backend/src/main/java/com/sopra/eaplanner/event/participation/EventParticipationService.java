package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventParticipationService {
    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    public void confirmAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        System.out.println("im in event participation and set confirmed");
        eventParticipation.setConfirmed(true);
        eventParticipation.setConfirmationTime(LocalDateTime.now());
        eventParticipationRepository.save(eventParticipation);
    }

    public void createAttendance(User user, Event event) {
        eventParticipationRepository.save(new EventParticipation(user, event));
    }

    public void deleteAttendance(User user, Event event) {
        eventParticipationRepository.delete(new EventParticipation(user, event));
    }
}
