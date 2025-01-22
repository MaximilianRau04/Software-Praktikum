package com.sopra.eaplanner.feedback;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundaryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.CommentType;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.CommentAnalysis;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO.FeedbackStatistics;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.UserTagWeight;
import com.sopra.eaplanner.user.UserTagWeightRepository;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;
import jakarta.persistence.EntityNotFoundException;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private UserTagWeightRepository userTagWeightRepository;

    @Autowired
    private EventParticipationService eventParticipationService;

    // Comment Mappers
    private final Function<FeedbackResponseDTO, CommentAnalysis> ENJOYMENT_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getEnjoymentComment(), analyzeSentiment(f.getEnjoymentComment()), f.getId(), f.getAuthor());
    private final Function<FeedbackResponseDTO, CommentAnalysis> IMPROVEMENT_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getImprovementComment(), analyzeSentiment(f.getImprovementComment()), f.getId(), f.getAuthor());
    private final Function<FeedbackResponseDTO, CommentAnalysis> REQUEST_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getRequestComment(), analyzeSentiment(f.getRequestComment()), f.getId(), f.getAuthor());
    private final Function<FeedbackResponseDTO, CommentAnalysis> PERSONAL_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getPersonalImprovementComment(), analyzeSentiment(f.getPersonalImprovementComment()), f.getId(), f.getAuthor());
    private final Function<FeedbackResponseDTO, CommentAnalysis> RECOMMENDATION_MAPPER = (FeedbackResponseDTO f) -> new CommentAnalysis(f.getRecommendationComment(), analyzeSentiment(f.getRecommendationComment()), f.getId(), f.getAuthor());

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
    @Autowired
    private TrainerProfileRepository trainerProfileRepository;

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

        if (feedback.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no feedback for this event yet, come back at a later time.");
        }

        Map<FeedbackType, FeedbackStatistics> numericalStats = new HashMap<>();

        List<Integer> allOverallScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getOverallScore);
        List<Integer> allOrganisationalScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getOrganisationalScore);
        List<Integer> allRelevanceScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getRelevanceScore);
        List<Integer> allUnderstandabilityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getUnderstandabilityScore);
        List<Integer> allContentDepthScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getContentDepthScore);
        List<Integer> allPracticalityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getPracticalityScore);
        List<Integer> allReasonabilityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getReasonabilityScore);
        List<Integer> allCompetencyScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getCompetencyScore);
        List<Integer> allPresentabilityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getPresentabilityScore);
        List<Integer> allInteractivityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getInteractivityScore);
        List<Integer> allTimeManagementScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getTimeManagementScore);
        List<Integer> allParticipationScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getParticipationScore);
        List<Integer> allAtmosphereScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getAtmosphereScore);
        List<Integer> allNetworkingScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getNetworkingScore);
        List<Integer> allEquipmentScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getEquipmentScore);
        List<Integer> allComfortabilityScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getComfortabilityScore);
        List<Integer> allCommunicationScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getCommunicationScore);
        List<Integer> allSimilarEventParticipationScores = extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getSimilarEventParticipationScore);

        List<List<Integer>> overallCategoryScores = List.of(allOverallScores, allOrganisationalScores, allRelevanceScores);
        List<List<Integer>> contentAndStructureScores = List.of(allUnderstandabilityScores, allContentDepthScores, allPracticalityScores, allReasonabilityScores);
        List<List<Integer>> trainerScores = List.of(allCompetencyScores, allPresentabilityScores, allInteractivityScores, allTimeManagementScores);
        List<List<Integer>> participationScores = List.of(allParticipationScores, allAtmosphereScores, allNetworkingScores);
        List<List<Integer>> itAndOrganisationScores = List.of(allEquipmentScores, allComfortabilityScores, allCommunicationScores);
        List<List<Integer>> similarityScores = List.of(allSimilarEventParticipationScores);

        FeedbackStatistics overall = createFeedbackStatistics(FeedbackType.OVERALL, overallCategoryScores);
        FeedbackStatistics contentAndStructure = createFeedbackStatistics(FeedbackType.CONTENT_AND_STRUCTURE, contentAndStructureScores);
        FeedbackStatistics trainer = createFeedbackStatistics(FeedbackType.TRAINER, trainerScores);
        FeedbackStatistics participation = createFeedbackStatistics(FeedbackType.PARTICIPATION, participationScores);
        FeedbackStatistics itAndOrganisation = createFeedbackStatistics(FeedbackType.IT_AND_ORGANISATION, itAndOrganisationScores);
        FeedbackStatistics similarity = createFeedbackStatistics(FeedbackType.SIMILARITY, similarityScores);

        numericalStats.put(FeedbackType.OVERALL, overall);
        numericalStats.put(FeedbackType.CONTENT_AND_STRUCTURE, contentAndStructure);
        numericalStats.put(FeedbackType.TRAINER, trainer);
        numericalStats.put(FeedbackType.PARTICIPATION, participation);
        numericalStats.put(FeedbackType.IT_AND_ORGANISATION, itAndOrganisation);
        numericalStats.put(FeedbackType.SIMILARITY, similarity);

        summary.setNumericalFeedback(numericalStats);

        Map<CommentType, List<CommentAnalysis>> comments = groupFeedbackCommentsByType(feedback);
        summary.setComments(comments);

        return summary;
    }

    public Resource getWordCloud(Long eventId) throws IOException {
        List<FeedbackResponseDTO> feedback = getFeedbacksFromEventId(eventId);
        BufferedImage wordCloudImage = generateWordCloud(feedback);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(wordCloudImage, "png", baos);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        return new InputStreamResource(inputStream);
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
        eventParticipationService.confirmFeedback(feedbackAuthor, eventForFeedback);

        double sentimentScore = getSentimentScoreFromPersonalFeedback(requestBody);
        double ratingScore = getFeedbackRating(requestBody);

        addTagWeightForFeedback(feedbackAuthor, eventForFeedback, sentimentScore, ratingScore);

        TrainerProfile profile = eventForFeedback.getOrganizer().getTrainerProfile();

        profile.setFeedbackCount(profile.getFeedbackCount() + 1);
        Integer feedbackCount = profile.getFeedbackCount();
        Double currentRating = profile.getAverageRating();
        Double newRating = (currentRating * (feedbackCount - 1) + ratingScore) / feedbackCount;
        profile.setAverageRating(newRating);

        trainerProfileRepository.save(profile);


        return new FeedbackResponseDTO(feedbackToSave);
    }

    private void addTagWeightForFeedback(User user, Event event, Double sentiment, Double rating) {
        for (Tag tag : event.getTags()) {
            UserTagWeight tagWeight = userTagWeightRepository.findByUserAndTag(user, tag)
                    .orElseGet(() -> new UserTagWeight(user, tag));

            tagWeight.setGivenFeedbackWithTag(tagWeight.getGivenFeedbackWithTag() + 1);
            int feedbackCount = tagWeight.getGivenFeedbackWithTag();

            Double currentSentiment = tagWeight.getGivenFeedbackResponseSentiment();
            Double newSentiment = (currentSentiment * (feedbackCount - 1) + sentiment) / feedbackCount;
            tagWeight.setGivenFeedbackResponseSentiment(newSentiment);

            Double currentRating = tagWeight.getGivenFeedbackRating();
            Double newRating = (currentRating * (feedbackCount - 1) + rating) / feedbackCount;
            tagWeight.setGivenFeedbackRating(newRating);

            userTagWeightRepository.save(tagWeight);
        }
    }

    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new EntityNotFoundException("Feedback with id " + id + " not found");
        }
        feedbackRepository.deleteById(id);
    }

    private Map<String, Double> getSubAveragesByType(FeedbackType type, List<List<Integer>> scores) {
        switch (type) {
            case OVERALL -> {
                Map<String, Double> overall = new HashMap<>();
                overall.put("overall", calculateAverage(scores.get(0)));
                overall.put("organisational", calculateAverage(scores.get(1)));
                overall.put("relevance", calculateAverage(scores.get(2)));
                return overall;
            }
            case CONTENT_AND_STRUCTURE -> {
                Map<String, Double> contentAndStructure = new HashMap<>();
                contentAndStructure.put("understandability", calculateAverage(scores.get(0)));
                contentAndStructure.put("contentDepth", calculateAverage(scores.get(1)));
                contentAndStructure.put("practicality", calculateAverage(scores.get(2)));
                contentAndStructure.put("reasonability", calculateAverage(scores.get(3)));
                return contentAndStructure;
            }
            case TRAINER -> {
                Map<String, Double> trainer = new HashMap<>();
                trainer.put("competency", calculateAverage(scores.get(0)));
                trainer.put("presentability", calculateAverage(scores.get(1)));
                trainer.put("interactivity", calculateAverage(scores.get(2)));
                trainer.put("timeManagement", calculateAverage(scores.get(3)));
                return trainer;
            }
            case PARTICIPATION -> {
                Map<String, Double> participation = new HashMap<>();
                participation.put("participation", calculateAverage(scores.get(0)));
                participation.put("atmosphere", calculateAverage(scores.get(1)));
                participation.put("networking", calculateAverage(scores.get(2)));
                return participation;
            }
            case IT_AND_ORGANISATION -> {
                Map<String, Double> itAndOrganisation = new HashMap<>();
                itAndOrganisation.put("equipment", calculateAverage(scores.get(0)));
                itAndOrganisation.put("comfortability", calculateAverage(scores.get(1)));
                itAndOrganisation.put("communication", calculateAverage(scores.get(2)));
                return itAndOrganisation;
            }
            case SIMILARITY -> {
                Map<String, Double> similarity = new HashMap<>();
                similarity.put("similarEventParticipation", calculateMedian(scores.get(0)));
                return similarity;
            }
            default -> throw new IllegalArgumentException("Unsupported feedback type: " + type);
        }
    }

    private FeedbackStatistics createFeedbackStatistics(FeedbackType type, List<List<Integer>> scores) {
        double average = calculateAveragesOfAverages(scores);
        double median = calculateMedianOfMedians(scores);
        int responseCount = scores.getFirst() != null ? scores.getFirst().size() : 0;
        Map<String, Double> subMedians = getSubAveragesByType(type, scores);

        return new FeedbackStatistics(average, median, responseCount, subMedians);
    }

    private double calculateAverage(List<Integer> scores) {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    private double calculateAveragesOfAverages(List<List<Integer>> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }
        double average = 0;

        for (List<Integer> categoryScores : scores) {
            average += categoryScores.stream().mapToInt(Integer::intValue).average().orElse(0);
        }
        return average / scores.size();
    }

    private double calculateMedian(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }
        if (scores.size() == 1) {
            return scores.getFirst().doubleValue();
        }

        List<Integer> mediansScoreList = new ArrayList<>(scores);
        Collections.sort(mediansScoreList);

        int size = mediansScoreList.size();
        if (size % 2 == 0) {
            return (mediansScoreList.get(size / 2 - 1) + mediansScoreList.get(size / 2)) / 2.0;
        } else {
            return mediansScoreList.get(size / 2);
        }
    }

    private double calculateMedianOfMedians(List<List<Integer>> scores) {
        List<Double> categoryMedians = new ArrayList<>();
        for (List<Integer> categoryScores : scores) {
            if (categoryScores == null || categoryScores.isEmpty()) {
                categoryMedians.add(0.0);
                continue;
            }
            if (categoryScores.size() == 1) {
                categoryMedians.add(categoryScores.getFirst().doubleValue());
                continue;
            }

            List<Integer> scoreList = new ArrayList<>(categoryScores);
            Collections.sort(scoreList);

            int size = scoreList.size();
            if (size % 2 == 0) {
                categoryMedians.add((scoreList.get(size / 2 - 1) + scoreList.get(size / 2)) / 2.0);
            } else {
                categoryMedians.add((double) scoreList.get(size / 2));
            }
        }
        List<Double> mediansScoreList = new ArrayList<>(categoryMedians);
        Collections.sort(mediansScoreList);

        int size = mediansScoreList.size();
        if (size % 2 == 0) {
            return (mediansScoreList.get(size / 2 - 1) + mediansScoreList.get(size / 2)) / 2.0;
        } else {
            return mediansScoreList.get(size / 2);
        }
    }

    private BufferedImage generateWordCloud(List<FeedbackResponseDTO> feedback) throws IOException {
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
        final WordCloud wordCloud = setupWordCloudBase();
        wordCloud.build(wordFrequencies);

        return wordCloud.getBufferedImage();
    }

    private WordCloud setupWordCloudBase() throws IOException {
        final Dimension dimension = new Dimension(900, 150);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
        wordCloud.setPadding(0);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("wordCloudMask.png");
        if (inputStream == null) {
            throw new FileNotFoundException("wordCloudMask.png not found in resources");
        }
        wordCloud.setBackground(new PixelBoundaryBackground(inputStream));
        wordCloud.setBackgroundColor(Color.WHITE);
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new LinearFontScalar(12, 25));
        return wordCloud;
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

    private List<CommentAnalysis> extractCommentsByType(List<FeedbackResponseDTO> feedback, Function<FeedbackResponseDTO, CommentAnalysis> mapper) {
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

    private <T> List<Integer> extractFeedbackScoresFromFeedback(List<FeedbackResponseDTO> feedback,
                                                                Function<FeedbackResponseDTO, T> extractor) {
        return feedback.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(val -> (Integer) val)
                .collect(Collectors.toList());
    }

    private Double getSentimentScoreFromPersonalFeedback(FeedbackRequestDTO f) {
        double sentiment = 0.0;

        final double enjoymentWeight = 0.4;
        final double improvementWeight = 0.1;
        final double requestWeight = 0.05;
        final double personalImprovementWeight = 0.1;
        final double recommendationWeight = 0.4;

        sentiment += enjoymentWeight * SentimentAnalyzer.getScoresFor(f.getEnjoymentComment()).getCompoundPolarity();
        sentiment += improvementWeight * SentimentAnalyzer.getScoresFor(f.getImprovementComment()).getCompoundPolarity();
        sentiment += requestWeight * SentimentAnalyzer.getScoresFor(f.getRequestComment()).getCompoundPolarity();
        sentiment += personalImprovementWeight * SentimentAnalyzer.getScoresFor(f.getPersonalImprovementComment()).getCompoundPolarity();
        sentiment += recommendationWeight * SentimentAnalyzer.getScoresFor(f.getRecommendationComment()).getCompoundPolarity();

        return sentiment;
    }

    private Double getFeedbackRating(FeedbackRequestDTO f) {
        double rating = 0.0;

        //Overall
        final double overallWeight = 0.4;
        final double organisationalWeight = 0.2;
        final double relevanceWeight = 0.4;

        // Content and structure
        final double understandabilityWeight = 0.3;
        final double contentDepthWeight = 0.4;
        final double practicalityWeight = 0.2;
        final double reasonabilityWeight = 0.1;

        // Trainer
        final double competencyWeight = 0.25;
        final double presentabilityWeight = 0.25;
        final double interactivityWeight = 0.25;
        final double timeManagementWeight = 0.25;

        // Participation
        final double participationWeight = 0.5;
        final double atmosphereSWeight = 0.15;
        final double networkingWeight = 0.35;

        // IT and Organisation
        final double equipmentWeight = 0.33;
        final double comfortabilityWeight = 0.33;
        final double communicationWeight = 0.33;

        rating += overallWeight * f.getOverallScore()
                + organisationalWeight * f.getOrganisationalScore()
                + relevanceWeight * f.getRelevanceScore();
        rating += understandabilityWeight * f.getUnderstandabilityScore()
                + contentDepthWeight * f.getContentDepthScore()
                + practicalityWeight * f.getPracticalityScore()
                + reasonabilityWeight * f.getReasonabilityScore();
        rating += competencyWeight * f.getCompetencyScore()
                + presentabilityWeight * f.getPresentabilityScore()
                + interactivityWeight * f.getInteractivityScore()
                + timeManagementWeight * f.getTimeManagementScore();
        rating += participationWeight * f.getParticipationScore()
                + atmosphereSWeight * f.getAtmosphereScore()
                + networkingWeight * f.getNetworkingScore();
        rating += equipmentWeight * f.getEquipmentScore()
                + communicationWeight * f.getCommunicationScore()
                + comfortabilityWeight * f.getComfortabilityScore();

        rating += f.getIsEventRecommended() ? 2.5 : -5;

        double normalizedRating = rating / 6.0;

        return Math.min(5.0, Math.max(1.0, normalizedRating));
    }
}