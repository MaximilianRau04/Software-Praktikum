package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.CommentAnalysis;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.FeedbackStatistics;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public FeedbackSummaryDTO generateFeedbackSummary(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        List<FeedbackResponseDTO> feedback = getFeedbacksFromEventId(eventId);

        FeedbackSummaryDTO summary = new FeedbackSummaryDTO(
                eventId,
                event.getName(),
                event.getOrganizer().getFirstname() + event.getOrganizer().getLastname());

        Map<String, FeedbackStatistics> numericalStats = new HashMap<>();
        List<Integer> overallScores = feedback.stream().map(FeedbackResponseDTO::getOverallScore).toList();
        numericalStats.put("overallScore", calculateStatistics(overallScores));

        // TODO: Implement all fields from FeedbackResponseDTO.class

        summary.setNumericalFeedback(numericalStats);

        List<CommentAnalysis> comments = feedback.stream()
                .map(userFeedback -> new CommentAnalysis(userFeedback.getImprovementComment(), analyzeSentiment(userFeedback.getImprovementComment()))).toList();

        // TODO: Maybe add some other fields around the comments that users give aside from improvement comment
        summary.setComments(comments);

        summary.setCommonWords(generateWordCloud(feedback));

        return summary;
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

    private FeedbackStatistics calculateStatistics(List<Integer> scores) {
        double average = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        double median = calculateMedian(scores);
        return new FeedbackStatistics(average, median, scores.size());
    }

    private List<String> generateWordCloud(List<FeedbackResponseDTO> feedback) {
        Map<String, Long> wordFrequency = feedback.stream()
                .flatMap(f -> Arrays.stream(f.getImprovementComment().split("\\s+")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(10)
                .toList();
    }

    private double calculateMedian(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        Collections.sort(scores);

        int size = scores.size();
        if (size % 2 == 0) {
            return (scores.get(size / 2 - 1) + scores.get(size / 2)) / 2.0;
        } else {
            return scores.get(size / 2);
        }
    }

    // TODO: Find library that can extract the general vibe from a String provided.
    private String analyzeSentiment(String text) {
        return "Not imlemented yet.";
    }
}
