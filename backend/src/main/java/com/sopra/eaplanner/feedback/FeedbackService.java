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
import com.sopra.eaplanner.event.tags.TagResponseDTO;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling feedback-related operations.
 * This class provides methods for generating feedback summaries, managing feedback creation and deletion,
 * generating word clouds from feedback comments, and retrieving feedback authors.
 */
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

    /**
     * Retrieves all feedback entries from the repository and converts them into DTOs.
     *
     * @return An iterable list of {@link FeedbackResponseDTO} containing all feedback entries.
     */
    public Iterable<FeedbackResponseDTO> getAllFeedbacks() {
        Iterable<Feedback> feedbacks = feedbackRepository.findAll();
        List<FeedbackResponseDTO> dtos = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            dtos.add(new FeedbackResponseDTO(feedback));
        }
        return dtos;
    }

    /**
     * Retrieves feedback entries associated with a specific event ID.
     *
     * @param eventId The ID of the event for which feedbacks are to be retrieved.
     * @return A list of {@link FeedbackResponseDTO} containing the feedbacks for the specified event.
     */
    public List<FeedbackResponseDTO> getFeedbacksFromEventId(Long eventId) {
        return feedbackRepository.findByEventId(eventId).stream()
                .map(FeedbackResponseDTO::new)
                .toList();
    }

    /**
     * Retrieves a specific feedback by its ID.
     *
     * @param id The ID of the feedback to be retrieved.
     * @return A {@link FeedbackResponseDTO} containing the feedback details.
     * @throws EntityNotFoundException if the feedback with the specified ID is not found.
     */
    public FeedbackResponseDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found."));
        return new FeedbackResponseDTO(feedback);
    }

    /**
     * Generates a summary of feedback for a specific event.
     * The summary includes numerical feedback for different categories and grouped comments by type.
     *
     * @param eventId The ID of the event for which the feedback summary is to be generated.
     * @return A {@link FeedbackSummaryDTO} containing the feedback summary for the event.
     * @throws EntityNotFoundException if the event with the specified ID is not found.
     * @throws ResponseStatusException if no feedback exists for the event.
     */
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

        // Extract feedback scores for each category
        List<Integer> allOverallScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getOverallScore);
        List<Integer> allOrganisationalScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getOrganisationalScore);
        List<Integer> allRelevanceScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getRelevanceScore);
        List<Integer> allUnderstandabilityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getUnderstandabilityScore);
        List<Integer> allContentDepthScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getContentDepthScore);
        List<Integer> allPracticalityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getPracticalityScore);
        List<Integer> allReasonabilityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getReasonabilityScore);
        List<Integer> allCompetencyScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getCompetencyScore);
        List<Integer> allPresentabilityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getPresentabilityScore);
        List<Integer> allInteractivityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getInteractivityScore);
        List<Integer> allTimeManagementScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getTimeManagementScore);
        List<Integer> allParticipationScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getParticipationScore);
        List<Integer> allAtmosphereScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getAtmosphereScore);
        List<Integer> allNetworkingScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getNetworkingScore);
        List<Integer> allEquipmentScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getEquipmentScore);
        List<Integer> allComfortabilityScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getComfortabilityScore);
        List<Integer> allCommunicationScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getCommunicationScore);
        List<Integer> allSimilarEventParticipationScores = FeedbackUtil.extractFeedbackScoresFromFeedback(feedback, FeedbackResponseDTO::getSimilarEventParticipationScore);

        List<List<Integer>> overallCategoryScores = List.of(allOverallScores, allOrganisationalScores, allRelevanceScores);
        List<List<Integer>> contentAndStructureScores = List.of(allUnderstandabilityScores, allContentDepthScores, allPracticalityScores, allReasonabilityScores);
        List<List<Integer>> trainerScores = List.of(allCompetencyScores, allPresentabilityScores, allInteractivityScores, allTimeManagementScores);
        List<List<Integer>> participationScores = List.of(allParticipationScores, allAtmosphereScores, allNetworkingScores);
        List<List<Integer>> itAndOrganisationScores = List.of(allEquipmentScores, allComfortabilityScores, allCommunicationScores);
        List<List<Integer>> similarityScores = List.of(allSimilarEventParticipationScores);

        // Create statistics for each category
        FeedbackStatistics overall = FeedbackUtil.createFeedbackStatistics(FeedbackType.OVERALL, overallCategoryScores);
        FeedbackStatistics contentAndStructure = FeedbackUtil.createFeedbackStatistics(FeedbackType.CONTENT_AND_STRUCTURE, contentAndStructureScores);
        FeedbackStatistics trainer = FeedbackUtil.createFeedbackStatistics(FeedbackType.TRAINER, trainerScores);
        FeedbackStatistics participation = FeedbackUtil.createFeedbackStatistics(FeedbackType.PARTICIPATION, participationScores);
        FeedbackStatistics itAndOrganisation = FeedbackUtil.createFeedbackStatistics(FeedbackType.IT_AND_ORGANISATION, itAndOrganisationScores);
        FeedbackStatistics similarity = FeedbackUtil.createFeedbackStatistics(FeedbackType.SIMILARITY, similarityScores);

        numericalStats.put(FeedbackType.OVERALL, overall);
        numericalStats.put(FeedbackType.CONTENT_AND_STRUCTURE, contentAndStructure);
        numericalStats.put(FeedbackType.TRAINER, trainer);
        numericalStats.put(FeedbackType.PARTICIPATION, participation);
        numericalStats.put(FeedbackType.IT_AND_ORGANISATION, itAndOrganisation);
        numericalStats.put(FeedbackType.SIMILARITY, similarity);

        summary.setNumericalFeedback(numericalStats);

        // Group comments by type
        Map<CommentType, List<CommentAnalysis>> comments = FeedbackUtil.groupFeedbackCommentsByType(feedback);
        summary.setComments(comments);

        Set<User> attendees = event.getRegisteredUsers();
        Set<Tag> eventTags = event.getTags();

        Map<String, FeedbackSummaryDTO.TagStatistic> tagStatisticsMap = new HashMap<>();

        if (!eventTags.isEmpty() && !attendees.isEmpty()) {
            List<UserTagWeight> userTagWeights = userTagWeightRepository.findByUsersAndTags(attendees, eventTags);
            Map<Tag, List<UserTagWeight>> tagWeightsMap = userTagWeights.stream()
                    .collect(Collectors.groupingBy(UserTagWeight::getTag));

            for (Tag tag : eventTags) {
                List<UserTagWeight> weightsForTag = tagWeightsMap.getOrDefault(tag, Collections.emptyList());
                FeedbackSummaryDTO.TagStatistic stats = calculateTagStatistics(tag, weightsForTag);
                tagStatisticsMap.put(tag.getName(), stats);
            }
        } else {
            for (Tag tag : eventTags) {
                tagStatisticsMap.put(tag.getName(), new FeedbackSummaryDTO.TagStatistic(new TagResponseDTO(tag), 0, 0, 0.0, 0.0, 0.0));
            }
        }

        summary.setTagStatistics(tagStatisticsMap);

        return summary;
    }

    private FeedbackSummaryDTO.TagStatistic calculateTagStatistics(Tag tag, List<UserTagWeight> userTagWeights) {
        int totalVisits = userTagWeights.stream().mapToInt(UserTagWeight::getVisitedEventsWithTag).sum();
        int totalFeedback = userTagWeights.stream().mapToInt(UserTagWeight::getGivenFeedbackWithTag).sum();

        double totalSentiment = 0.0;
        double totalRating = 0.0;
        for (UserTagWeight utw : userTagWeights) {
            totalSentiment += utw.getGivenFeedbackResponseSentiment() * utw.getGivenFeedbackWithTag();
            totalRating += utw.getGivenFeedbackRating() * utw.getGivenFeedbackWithTag();
        }

        double averageSentiment = totalFeedback > 0 ? totalSentiment / totalFeedback : 0.0;
        double averageRating = totalFeedback > 0 ? totalRating / totalFeedback : 0.0;

        double averageWeight = userTagWeights.stream()
                .mapToDouble(UserTagWeight::getTagWeight)
                .average()
                .orElse(0.0);

        return new FeedbackSummaryDTO.TagStatistic(new TagResponseDTO(tag), totalVisits, totalFeedback, averageSentiment, averageRating, averageWeight);
    }

    /**
     * Generates a word cloud based on feedback comments for a specific event.
     * The word cloud is returned as a {@link Resource} containing a PNG image of the word cloud.
     *
     * @param eventId The ID of the event for which the word cloud is to be generated.
     * @return A {@link Resource} containing the generated word cloud image.
     * @throws IOException if there is an error while generating the word cloud image.
     */
    public Resource getWordCloud(Long eventId) throws IOException {
        List<FeedbackResponseDTO> feedback = getFeedbacksFromEventId(eventId);
        BufferedImage wordCloudImage = generateWordCloud(feedback);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(wordCloudImage, "png", baos);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        return new InputStreamResource(inputStream);
    }

    /**
     * Retrieves the author of a specific feedback by its ID.
     * If the feedback is anonymous, an exception is thrown.
     *
     * @param id The ID of the feedback whose author is to be retrieved.
     * @return A {@link UserResponseDTO} containing the details of the feedback author.
     * @throws EntityNotFoundException if the feedback is anonymous or not found.
     */
    public UserResponseDTO getFeedbackAuthor(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        if (feedback.isAnonymousFeedback()) {
            throw new EntityNotFoundException("Anonymous Feedback");
        }

        return new UserResponseDTO(feedback.getUser());
    }

    /**
     * Creates and saves a new feedback entry based on the provided request data.
     * The method also updates the feedback statistics for the trainer and event.
     *
     * @param requestBody The request data for creating the feedback.
     * @return A {@link FeedbackResponseDTO} containing the details of the created feedback.
     * @throws EntityNotFoundException if the event or user related to the feedback is not found.
     */
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO requestBody) {
        Event eventForFeedback = eventRepository.findById(requestBody.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        User feedbackAuthor = userRepository.findById(requestBody.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        double sentimentScore = FeedbackUtil.getSentimentScoreFromPersonalFeedback(requestBody);
        double ratingScore = FeedbackUtil.getFeedbackRating(requestBody);
        Feedback feedbackToSave = new Feedback(requestBody, eventForFeedback, feedbackAuthor, ratingScore);

        addTagWeightForFeedback(feedbackAuthor, eventForFeedback, sentimentScore, ratingScore);

        TrainerProfile profile = eventForFeedback.getOrganizer().getTrainerProfile();

        feedbackToSave.setSentiment(sentimentScore);
        feedbackToSave.setTrainerProfile(profile);
        eventParticipationService.confirmFeedback(feedbackAuthor, eventForFeedback);

        feedbackToSave = feedbackRepository.save(feedbackToSave);

        User organizer = eventForFeedback.getOrganizer();
        List<Feedback> allFeedbacks = feedbackRepository.findByEventOrganizer(organizer);

        // Adjustable decayRate needs to be tested and agreed upon for relevance over the chosen timeframes
        // We choose 0.02 as the basis in order to have relevant feedback data up to a couple months in the past
        double decayRate = 0.02;
        double totalWeightedSum = 0.0;
        double totalWeight = 0.0;
        LocalDateTime now = LocalDateTime.now();

        // We use exponential decay for older feedback in order to keep current status of the trainers more relevant
        // eg. some bad events 1 year ago shouldn't tank the star rating of an organizer if it got better.
        for (Feedback feedback : allFeedbacks) {
            long daysOld = Duration.between(feedback.getCreatedAt(), now).toDays();
            double weight = Math.exp(-decayRate * daysOld);
            totalWeightedSum += feedback.getRating() * weight;
            totalWeight += weight;
        }

        double newAverage = totalWeightedSum / totalWeight;

        profile.setAverageRating(newAverage);
        profile.setFeedbackCount(allFeedbacks.size());

        trainerProfileRepository.save(profile);
        eventRepository.save(eventForFeedback);
        userRepository.save(feedbackAuthor);

        return new FeedbackResponseDTO(feedbackToSave);
    }

    /**
     * Deletes a feedback entry by its ID.
     *
     * @param id The ID of the feedback to be deleted.
     * @throws EntityNotFoundException if the feedback with the specified ID does not exist.
     */
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new EntityNotFoundException("Feedback with id " + id + " not found");
        }
        feedbackRepository.deleteById(id);
    }

    /**
     * Updates the tag weights for a user based on their feedback.
     * This method adjusts sentiment and rating for each tag associated with the event.
     *
     * @param user      The user providing the feedback.
     * @param event     The event for which the feedback is provided.
     * @param sentiment The sentiment score associated with the feedback.
     * @param rating    The rating score associated with the feedback.
     */
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

    /**
     * Generates a word cloud image based on the feedback comments.
     * The comments are tokenized, filtered to remove stopwords, and then used to build a frequency analysis of the words.
     * The word frequencies are then used to create a word cloud image.
     *
     * @param feedback The list of feedback entries from which to extract comments for the word cloud.
     * @return A {@link BufferedImage} representing the generated word cloud.
     * @throws IOException if an error occurs while reading the background mask image or generating the word cloud.
     */
    private BufferedImage generateWordCloud(List<FeedbackResponseDTO> feedback) throws IOException {
        List<String> comments = new ArrayList<>();

        feedback.forEach((f) -> comments.addAll(f.getAllComments()));

        String allComments = String.join("\n", comments).toLowerCase();

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(allComments);

        List<String> filteredTokens = Arrays.stream(tokens)
                .map(word -> word.replaceAll("[^a-zA-Z1-9]", ""))
                .filter(word -> !FeedbackUtil.stopWords.contains(word) && word.length() > 2 && word.length() <= 15)
                .toList();

        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(filteredTokens);
        final WordCloud wordCloud = setupWordCloudBase();
        wordCloud.build(wordFrequencies);

        return wordCloud.getBufferedImage();
    }

    /**
     * Sets up the base configuration for the word cloud generation, including dimensions, background, color palette, and font scaling.
     * The method uses a mask image for the word cloud's background.
     *
     * @return A {@link WordCloud} object with the configured settings for building a word cloud.
     * @throws IOException if the mask image cannot be found or loaded from the resources.
     */
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
}