package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.reward.RewardService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserTagWeight;
import com.sopra.eaplanner.user.UserTagWeightRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class EventParticipationService {
    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    private UserTagWeightRepository userTagWeightRepository;

    @Autowired
    private RewardService rewardService;

    public void createAttendance(User user, Event event) {
        if (eventParticipationRepository.existsByUserAndEvent(user, event)) {
            throw new EntityExistsException("Event is already being attended.");
        }

        rewardService.createAttendanceAndFeedbackRewards(user);
        eventParticipationRepository.save(new EventParticipation(user, event));
    }

    public void confirmAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));

        if(eventParticipation.getIsParticipationConfirmed()){
            return;
        }

        if(eventParticipation.getFeedbackGiven()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has already given Feedback.");
        }

        eventParticipation.setIsParticipationConfirmed(true);
        eventParticipation.setConfirmationTime(LocalDateTime.now());
        eventParticipationRepository.save(eventParticipation);

        addTagWeightForAttendance(user, event);

        rewardService.grantAttendancePoints(user);

        if(user.getTagWeights().stream().filter(w -> w.getVisitedEventsWithTag() >= 3).toList().size() >= 5){
            rewardService.grantAllrounderReward(user);
        }
    }

    public void deleteAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        eventParticipationRepository.delete(eventParticipation);
    }

    public void confirmFeedback(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));

        eventParticipation.setFeedbackGiven(true);
        eventParticipation.setFeedbackTime(LocalDateTime.now());
        eventParticipationRepository.save(eventParticipation);

        rewardService.grantFeedbackGiverPoints(user);

        long feedbackCount = eventParticipationRepository.countByUserAndFeedbackGivenTrue(user);

        if(feedbackCount >= 20){
            rewardService.grantCleanSubmitterReward(user);
        }
    }

    private void addTagWeightForAttendance(User user, Event event) {
        for (Tag tag: event.getTags()){
            UserTagWeight tagWeight = userTagWeightRepository.findByUserAndTag(user, tag)
                    .orElseGet(()-> new UserTagWeight(user, tag));
            tagWeight.setVisitedEventsWithTag(tagWeight.getVisitedEventsWithTag() + 1);
            userTagWeightRepository.save(tagWeight);
        }
    }
}
