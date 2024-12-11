package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.EventService;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    private Event eventForFeedback;

    public Iterable<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public List<FeedbackResponseDTO> getFeedbacksFromEventId(Long eventId) {
        List<Feedback> feedbacksForEvent = feedbackRepository.findByEventId(eventId);
        List<FeedbackResponseDTO> foundFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbacksForEvent) {
            foundFeedbacks.add(new FeedbackResponseDTO(feedback));
        }
        return foundFeedbacks;
    }

    public Feedback getFeedbackById(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new EntityNotFoundException("Feedback with id " + id + " not found");
        }
        return feedbackRepository.findById(id).get();
    }

    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO requestBody) {
        if (!eventRepository.existsById(requestBody.getEventId())) {
            throw new EntityNotFoundException("Event with id " + requestBody.getEventId() + " not found");
        }
        if (!userRepository.existsById(requestBody.getUserId())) {
            throw new EntityNotFoundException("User with id " + requestBody.getUserId() + " not found");
        }

        Event eventForFeedback = eventRepository.findById(requestBody.getEventId()).get();
        User organizerOfEvent = userRepository.findById(requestBody.getUserId()).get();

        Feedback feedbackToSave = new Feedback(requestBody, eventForFeedback, organizerOfEvent);
        feedbackToSave = feedbackRepository.save(feedbackToSave);
        return new FeedbackResponseDTO(feedbackToSave);
    }

    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new EntityNotFoundException("Feedback with id " + id + " not found");
        }
        feedbackRepository.deleteById(id);
    }
}
