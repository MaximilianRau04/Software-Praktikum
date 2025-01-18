package com.sopra.eaplanner.feedback;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.CommentType;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.CommentAnalysis;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.FeedbackStatistics;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;
import jakarta.persistence.EntityNotFoundException;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
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

    private final Function<FeedbackResponseDTO,CommentAnalysis> ENJOYMENT_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getEnjoymentComment(), analyzeSentiment(f.getEnjoymentComment()), f.getId());
    private final Function<FeedbackResponseDTO,CommentAnalysis> IMPROVEMENT_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getImprovementComment(), analyzeSentiment(f.getImprovementComment()), f.getId());
    private final Function<FeedbackResponseDTO,CommentAnalysis> REQUEST_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getRequestComment(), analyzeSentiment(f.getRequestComment()), f.getId());
    private final Function<FeedbackResponseDTO,CommentAnalysis> PERSONAL_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getPersonalImprovementComment(), analyzeSentiment(f.getPersonalImprovementComment()), f.getId());
    private final Function<FeedbackResponseDTO,CommentAnalysis> RECOMMENDATION_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getRecommendationComment(), analyzeSentiment(f.getRecommendationComment()), f.getId());

    /**
     * Set of stopWords for use in the word cloud generation as a preprocessing step after tokenization
     * word list imported from: <a href="https://gist.github.com/sebleier/554280">https://gist.github.com/sebleier/554280</a>
     */
    private final Set<String> stopWords = Set.of(
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your",
            "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she",
            "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
            "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an",
            "the", "and", "but", "if", "or", "because", "as", "until", "while", "of",
            "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "down",
            "in", "out", "on", "off", "over", "under", "again", "further", "then",
            "once", "here", "there", "when", "where", "why", "how", "all", "any",
            "both", "each", "few", "more", "most", "other", "some", "such", "no",
            "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s",
            "t", "can", "will", "just", "don", "should", "now"
    );

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
        summary.setCommonWords(generateWordCloud(feedback));

        Map<CommentType, List<CommentAnalysis>> comments = groupFeedbackCommentsByType(feedback);

        summary.setComments(comments);
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
        eventParticipationService.postFeedback(feedbackAuthor, eventForFeedback);

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
        List<String> comments = new ArrayList<>();

        feedback.forEach((f) -> comments.addAll(f.getAllComments()));

        String allComments = String.join("\n", comments).toLowerCase();

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(allComments);

        List<String> filteredTokens = Arrays.stream(tokens)
                .map(word -> word.replaceAll("[^a-zA-Z1-9]", ""))
                .filter(word -> !stopWords.contains(word) && word.length() > 2 && word.length() <= 15)
                .toList();

        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(filteredTokens);
        final Dimension dimension = new Dimension(600,600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
        wordCloud.build(wordFrequencies);

        File outputDir = new File("kumo-core/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // This will create the necessary directories
        }
        wordCloud.writeToFile("kumo-core/output/datarank_wordcloud_circle_sqrt_font.png");

        // Add a word filter in order to only take in adjectives, nouns and verbs in this order
        // as of right now, we have words like "is, was" etc. popping in which is not very helpful
        Map<String, Long> wordFrequency = feedback.stream()
                .flatMap(f -> Arrays.stream(f.getImprovementComment().split("\\s+")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(10)
                .toList();
    }

    private Map<CommentType, List<CommentAnalysis>> groupFeedbackCommentsByType(List<FeedbackResponseDTO> feedback) {
        Map<CommentType, List<CommentAnalysis>> comments = new HashMap<>();

        comments.put(CommentType.ENJOYMENT, extractCommentsByType(feedback, ENJOYMENT_MAPPER));
        comments.put(CommentType.IMPROVEMENT, extractCommentsByType(feedback, IMPROVEMENT_MAPPER));
        comments.put(CommentType.REQUEST, extractCommentsByType(feedback, REQUEST_MAPPER));
        comments.put(CommentType.PERSONAL_IMPROVEMENT, extractCommentsByType(feedback, PERSONAL_MAPPER));
        comments.put(CommentType.RECOMMENDATION, extractCommentsByType(feedback, RECOMMENDATION_MAPPER));
        return comments;
    }

    private List<CommentAnalysis> extractCommentsByType(List<FeedbackResponseDTO> feedback, Function<FeedbackResponseDTO,CommentAnalysis> mapper) {
        return feedback.stream()
                .map(mapper)
                .toList();
    }

    /**
     * Provides a sentiment categorization of the passed in text to be used in feedback display for the workshops
     *
     * <a href="https://github.com/apanimesh061/VaderSentimentJava">VADERSentimentJava Tool</a> used for Sentiment analysis
     * under MIT License
     *
     * @param text The comment to be analyzed for sentiment
     * @return Returns Strings "positive", "neutral" or "negative" based on the result of the
     * sentimentPolarities produced from the SentimentAnalyzer
     */
    private String analyzeSentiment(String text) {
        final SentimentPolarities sentimentPolarities = SentimentAnalyzer.getScoresFor(text);
        return assignSentimentToComment(sentimentPolarities);
    }

    private String assignSentimentToComment(SentimentPolarities sentimentPolarities) {
        if (sentimentPolarities.getCompoundPolarity() >= 0.2f) {
            return "positive";
        } else if (sentimentPolarities.getCompoundPolarity() < 0.2f && sentimentPolarities.getCompoundPolarity() >= -0.2f) {
            return "neutral";
        } else if (sentimentPolarities.getCompoundPolarity() < -0.2f) {
            return "negative";
        } else {
            return "neutral";
        }
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

}