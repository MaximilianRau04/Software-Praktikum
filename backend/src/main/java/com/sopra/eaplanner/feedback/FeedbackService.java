package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
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

    public Iterable<FeedbackResponseDTO> getAllFeedbacks() {
        Iterable<Feedback> feedbacks = feedbackRepository.findAll();
        List<FeedbackResponseDTO> dtos = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            dtos.add(new FeedbackResponseDTO(feedback));
        }
        return dtos;
    }

    public List<FeedbackResponseDTO> getFeedbacksFromEventId(Long eventId) {
        List<Feedback> feedbacksForEvent = feedbackRepository.findByEventId(eventId);
        List<FeedbackResponseDTO> foundFeedbacks = new ArrayList<>();
        for (Feedback feedback : feedbacksForEvent) {
            foundFeedbacks.add(new FeedbackResponseDTO(feedback));
        }
        return foundFeedbacks;
    }

    public FeedbackResponseDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found."));

        return new FeedbackResponseDTO(feedback);
    }

    public UserResponseDTO getFeedbackAuthor(Long id) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isEmpty()) {
            throw new EntityNotFoundException("Feedback not found");
        }

        if (feedback.get().isAnonymousFeedback()) {
            throw new EntityNotFoundException("Anonymous Feedback");
        }

        return new UserResponseDTO(feedback.get().getUser());
    }

    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO requestBody) {
        Optional<Event> eventForFeedback = eventRepository.findById(requestBody.getEventId());
        if (eventForFeedback.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }

        Optional<User> feedbackAuthor = userRepository.findById(requestBody.getUserId());
        if (feedbackAuthor.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        Feedback feedbackToSave = new Feedback(requestBody, eventForFeedback.get(), feedbackAuthor.get());
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
