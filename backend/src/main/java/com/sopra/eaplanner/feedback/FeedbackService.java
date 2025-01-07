package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.participation.EventParticipationService;
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

    @Autowired
    private EventParticipationService eventParticipationService;

    public Iterable<FeedbackResponseDTO> getAllFeedbacks() {
        Iterable<Feedback> feedbacks = feedbackRepository.findAll();
        List<FeedbackResponseDTO> dtos = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            dtos.add(new FeedbackResponseDTO(feedback));
        }
        return dtos;
    }

    public List<FeedbackResponseDTO> getFeedbacksFromEventId(Long eventId) {
        return feedbackRepository.findByEventId(eventId).stream()
                .map(FeedbackResponseDTO::new)
                .toList();
    }

    public FeedbackResponseDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found."));
        return new FeedbackResponseDTO(feedback);
    }

    public FeedbackSummaryDTO generateFeedbackSummary(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found."));
        List<FeedbackResponseDTO> feedback = getFeedbacksFromEventId(eventId);

        FeedbackSummaryDTO summary = new FeedbackSummaryDTO(
                eventId,
                event.getName(),
                event.getOrganizer().getFirstname() + " " + event.getOrganizer().getLastname()
        );

        Map<String, FeedbackStatistics> numericalStats = new HashMap<>();

        addStatistic(numericalStats, "overallScore", feedback, FeedbackResponseDTO::getOverallScore);
        addStatistic(numericalStats, "organisationalScore", feedback, FeedbackResponseDTO::getOrganisationalScore);
        addStatistic(numericalStats, "relevanceScore", feedback, FeedbackResponseDTO::getRelevanceScore);
        addStatistic(numericalStats, "understandabilityScore", feedback, FeedbackResponseDTO::getUnderstandabilityScore);
        addStatistic(numericalStats, "contentDepthScore", feedback, FeedbackResponseDTO::getContentDepthScore);
        addStatistic(numericalStats, "practicalityScore", feedback, FeedbackResponseDTO::getPracticalityScore);
        addStatistic(numericalStats, "reasonabilityScore", feedback, FeedbackResponseDTO::getReasonabilityScore);
        addStatistic(numericalStats, "competencyScore", feedback, FeedbackResponseDTO::getCompetencyScore);
        addStatistic(numericalStats, "presentabilityScore", feedback, FeedbackResponseDTO::getPresentabilityScore);
        addStatistic(numericalStats, "interactivityScore", feedback, FeedbackResponseDTO::getInteractivityScore);
        addStatistic(numericalStats, "timeManagementScore", feedback, FeedbackResponseDTO::getTimeManagementScore);
        addStatistic(numericalStats, "participationScore", feedback, FeedbackResponseDTO::getParticipationScore);
        addStatistic(numericalStats, "atmosphereScore", feedback, FeedbackResponseDTO::getAtmosphereScore);
        addStatistic(numericalStats, "networkingScore", feedback, FeedbackResponseDTO::getNetworkingScore);
        addStatistic(numericalStats, "equipmentScore", feedback, FeedbackResponseDTO::getEquipmentScore);
        addStatistic(numericalStats, "comfortabilityScore", feedback, FeedbackResponseDTO::getComfortabilityScore);
        addStatistic(numericalStats, "communicationScore", feedback, FeedbackResponseDTO::getCommunicationScore);
        addStatistic(numericalStats, "similarEventParticipationScore", feedback, FeedbackResponseDTO::getSimilarEventParticipationScore);

        summary.setNumericalFeedback(numericalStats);

        List<CommentAnalysis> comments = feedback.stream()
                .map(f -> new CommentAnalysis(f.getImprovementComment(), analyzeSentiment(f.getImprovementComment())))
                .toList();

        summary.setComments(comments);
        summary.setCommonWords(generateWordCloud(feedback));

        return summary;
    }

    public UserResponseDTO getFeedbackAuthor(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        if (feedback.isAnonymousFeedback()) {
            throw new EntityNotFoundException("Anonymous Feedback");
        }

        return new UserResponseDTO(feedback.getUser());
    }

    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO requestBody) {
        Event eventForFeedback = eventRepository.findById(requestBody.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        User feedbackAuthor = userRepository.findById(requestBody.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Feedback feedbackToSave = new Feedback(requestBody, eventForFeedback, feedbackAuthor);

        feedbackToSave = feedbackRepository.save(feedbackToSave);
        eventParticipationService.postFeedback(feedbackAuthor,eventForFeedback);

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

    private double calculateMedian(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        List<Integer> scoreList = new ArrayList<>(scores);
        Collections.sort(scoreList);

        int size = scoreList.size();
        if (size % 2 == 0) {
            return (scoreList.get(size / 2 - 1) + scoreList.get(size / 2)) / 2.0;
        } else {
            return scoreList.get(size / 2);
        }
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

    private <T> void addStatistic(Map<String, FeedbackStatistics> stats,
                                  String key,
                                  List<FeedbackResponseDTO> feedback,
                                  Function<FeedbackResponseDTO, T> extractor) {
        List<Integer> values = feedback.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(val -> (Integer) val)
                .collect(Collectors.toList());

        stats.put(key, calculateStatistics(values));
    }

    // TODO: Find library that can extract the general vibe from a String provided.
    private String analyzeSentiment(String text) {
        return "Not implemented yet.";
    }
}
