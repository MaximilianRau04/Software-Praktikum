package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.reward.RewardService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.UserTagWeight;
import com.sopra.eaplanner.user.UserTagWeightRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Service class for managing event participation operations.
 *
 * <p>This service handles the logic for creating, confirming, and deleting event attendance,
 * as well as processing user feedback and awarding related rewards. It uses the {@link EventParticipationRepository},
 * {@link UserTagWeightRepository}, and {@link RewardService} to manage the underlying data and rewards.</p>
 */
@Service
public class EventParticipationService {
    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    private UserTagWeightRepository userTagWeightRepository;

    @Autowired
    private RewardService rewardService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    /**
     * Creates a new attendance record for the user at the specified event.
     *
     * <p>This method checks if the user is already attending the event, and if not, it creates
     * a new attendance record. Additionally, it grants rewards related to attendance and feedback.</p>
     *
     * @param user the user whose attendance is being created.
     * @param event the event for which the user is attending.
     * @throws EntityExistsException if the user is already attending the event.
     */
    public void createAttendance(User user, Event event) {
        if (eventParticipationRepository.existsByUserAndEvent(user, event)) {
            throw new EntityExistsException("Event is already being attended.");
        }

        rewardService.createAttendanceAndFeedbackRewards(user);
        eventParticipationRepository.save(new EventParticipation(user, event));
    }

    /**
     * Deletes a user's attendance record for the specified event.
     *
     * <p>This method removes the user's participation from the event. If no participation record is found,
     * an exception is thrown.</p>
     *
     * @param user the user whose attendance is being deleted.
     * @param event the event from which the user is deleting their attendance.
     * @throws EntityNotFoundException if no participation record is found.
     */
    public void deleteAttendance(User user, Event event) {
        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));
        eventParticipationRepository.delete(eventParticipation);
    }

    /**
     * Confirms a user's attendance for the specified event.
     *
     * <p>This method marks the user's participation as confirmed if it hasn't been confirmed already.
     * It also checks whether the user has already given feedback and prevents confirmation if feedback is already provided.
     * Upon successful confirmation, it grants attendance points and updates the tag weights for the user based on the event's tags.</p>
     *
     * @param user the user whose attendance is being confirmed.
     * @param event the event for which the user's attendance is being confirmed.
     * @throws EntityNotFoundException if no participation record is found.
     * @throws ResponseStatusException if feedback has already been given by the user.
     */
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

    /**
     * Confirms the feedback given by a user for a specific event.
     *
     * <p>This method marks the feedback as given and updates the corresponding timestamp. It also grants reward points
     * to the user for providing feedback.</p>
     *
     * @param user the user whose feedback is being confirmed.
     * @param event the event for which the user's feedback is being confirmed.
     * @throws EntityNotFoundException if no participation record is found.
     */
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

    public EventParticipationDTO getParticipation(Long userId, Long eventId){
        User user = userRepository.findById(userId).orElseThrow(()-> new EntityNotFoundException(" User not found "));
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new EntityNotFoundException(" Event not found "));

        EventParticipation part = eventParticipationRepository.findByUserAndEvent(user, event).orElseThrow(()-> new EntityNotFoundException(" Participation not found "));

        return new EventParticipationDTO(part);
    }

    /**
     * Updates the tag weights for a user based on the tags associated with the event they attended.
     *
     * <p>This method increments the count of visited events for each tag associated with the event the user attended.
     * If the user has no tag weight for a particular tag, a new record is created.</p>
     *
     * @param user the user whose tag weights are being updated.
     * @param event the event whose tags are being processed.
     */
    private void addTagWeightForAttendance(User user, Event event) {
        for (Tag tag: event.getTags()){
            UserTagWeight tagWeight = userTagWeightRepository.findByUserAndTag(user, tag)
                    .orElseGet(()-> new UserTagWeight(user, tag));
            tagWeight.setVisitedEventsWithTag(tagWeight.getVisitedEventsWithTag() + 1);
            userTagWeightRepository.save(tagWeight);
        }
    }
}
