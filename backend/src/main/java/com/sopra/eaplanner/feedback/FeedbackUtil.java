package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.CommentType;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;

import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeedbackUtil {

    // Comment Mappers
    public static final Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> ENJOYMENT_MAPPER = (FeedbackResponseDTO f) -> new FeedbackSummaryDTO.CommentAnalysis(f.getEnjoymentComment(), analyzeSentiment(f.getEnjoymentComment()), f.getId(), f.getAuthor());
    public static final Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> IMPROVEMENT_MAPPER = (FeedbackResponseDTO f) -> new FeedbackSummaryDTO.CommentAnalysis(f.getImprovementComment(), analyzeSentiment(f.getImprovementComment()), f.getId(), f.getAuthor());
    public static final Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> REQUEST_MAPPER = (FeedbackResponseDTO f) -> new FeedbackSummaryDTO.CommentAnalysis(f.getRequestComment(), analyzeSentiment(f.getRequestComment()), f.getId(), f.getAuthor());
    public static final Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> PERSONAL_MAPPER = (FeedbackResponseDTO f) -> new FeedbackSummaryDTO.CommentAnalysis(f.getPersonalImprovementComment(), analyzeSentiment(f.getPersonalImprovementComment()), f.getId(), f.getAuthor());
    public static final Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> RECOMMENDATION_MAPPER = (FeedbackResponseDTO f) -> new FeedbackSummaryDTO.CommentAnalysis(f.getRecommendationComment(), analyzeSentiment(f.getRecommendationComment()), f.getId(), f.getAuthor());

    /**
     * This method calculates and returns the average (or median) scores for different sub-categories
     * based on the given feedback type. It supports different types of feedback, each with its own
     * sub-categories for which averages are computed.
     *
     * @param type   The {@link FeedbackType} that determines which sub-categories to compute the averages for.
     * @param scores A list of lists containing the feedback scores for the sub-categories. Each list in the
     *               main list corresponds to a specific sub-category.
     * @return A {@link Map} where the keys are the names of the sub-categories and the values are the
     * computed averages (or median for the SIMILARITY type).
     * @throws IllegalArgumentException If an unsupported feedback type is provided.
     */
    public static Map<String, Double> getSubAveragesByType(FeedbackType type, List<List<Integer>> scores) {
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
                similarity.put("similarEventParticipation", calculateMedian(scores.getFirst()));
                return similarity;
            }
            default -> throw new IllegalArgumentException("Unsupported feedback type: " + type);
        }
    }

    /**
     * Creates and returns the {@link FeedbackSummaryDTO.FeedbackStatistics} object containing
     * statistical data (average, median, response count, and sub-category medians) for the given feedback scores.
     *
     * @param type   The {@link FeedbackType} that determines which sub-categories to calculate statistics for.
     * @param scores A list of lists containing the feedback scores for different sub-categories.
     * @return A {@link FeedbackSummaryDTO.FeedbackStatistics} object containing the calculated statistics.
     */
    public static FeedbackSummaryDTO.FeedbackStatistics createFeedbackStatistics(FeedbackType type, List<List<Integer>> scores) {
        double average = calculateAveragesOfAverages(scores);
        double median = calculateMedianOfMedians(scores);
        int responseCount = scores.getFirst() != null ? scores.getFirst().size() : 0;
        Map<String, Double> subMedians = getSubAveragesByType(type, scores);

        return new FeedbackSummaryDTO.FeedbackStatistics(average, median, responseCount, subMedians);
    }

    /**
     * Calculates the average score of a list of integers.
     *
     * @param scores A list of integers representing the feedback scores.
     * @return The average score, or 0 if the list is empty.
     */
    public static double calculateAverage(List<Integer> scores) {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    /**
     * Calculates the average of averages for a list of lists of feedback scores.
     * This method computes the average score for each sub-category and then averages those averages.
     *
     * @param scores A list of lists, where each list contains feedback scores for a sub-category.
     * @return The average of averages, or 0 if the list is empty or contains no scores.
     */
    public static double calculateAveragesOfAverages(List<List<Integer>> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }
        double average = 0;

        for (List<Integer> categoryScores : scores) {
            average += categoryScores.stream().mapToInt(Integer::intValue).average().orElse(0);
        }
        return average / scores.size();
    }

    /**
     * Calculates the median score of a list of integers. The median is the middle value in the sorted list.
     * If the list has an even number of elements, the median is the average of the two middle values.
     *
     * @param scores A list of integers representing the feedback scores.
     * @return The median score, or 0 if the list is empty.
     */
    public static double calculateMedian(List<Integer> scores) {
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

    /**
     * Calculates the median of medians for a list of lists of feedback scores.
     * This method first calculates the median for each sub-category, then computes the median of those medians.
     *
     * @param scores A list of lists, where each list contains feedback scores for a sub-category.
     * @return The median of medians, or 0 if the list is empty or contains no scores.
     */
    public static double calculateMedianOfMedians(List<List<Integer>> scores) {
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

    public static Map<CommentType, List<FeedbackSummaryDTO.CommentAnalysis>> groupFeedbackCommentsByType(List<FeedbackResponseDTO> feedback) {
        Map<CommentType, List<FeedbackSummaryDTO.CommentAnalysis>> comments = new HashMap<>();

        comments.put(CommentType.ENJOYMENT, extractCommentsByType(feedback, FeedbackUtil.ENJOYMENT_MAPPER));
        comments.put(CommentType.IMPROVEMENT, extractCommentsByType(feedback, FeedbackUtil.IMPROVEMENT_MAPPER));
        comments.put(CommentType.REQUEST, extractCommentsByType(feedback, FeedbackUtil.REQUEST_MAPPER));
        comments.put(CommentType.PERSONAL_IMPROVEMENT, extractCommentsByType(feedback, FeedbackUtil.PERSONAL_MAPPER));
        comments.put(CommentType.RECOMMENDATION, extractCommentsByType(feedback, FeedbackUtil.RECOMMENDATION_MAPPER));
        return comments;
    }

    public static List<FeedbackSummaryDTO.CommentAnalysis> extractCommentsByType(List<FeedbackResponseDTO> feedback, Function<FeedbackResponseDTO, FeedbackSummaryDTO.CommentAnalysis> mapper) {
        return feedback.stream()
                .map(mapper)
                .filter(commentAnalysis -> !commentAnalysis.getComment().isEmpty())
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
    public static String analyzeSentiment(String text) {
        final SentimentPolarities sentimentPolarities = SentimentAnalyzer.getScoresFor(text);
        return assignSentimentToComment(sentimentPolarities);
    }

    public static String assignSentimentToComment(SentimentPolarities sentimentPolarities) {
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

    public static <T> List<Integer> extractFeedbackScoresFromFeedback(List<FeedbackResponseDTO> feedback,
                                                                      Function<FeedbackResponseDTO, T> extractor) {
        return feedback.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .map(val -> (Integer) val)
                .collect(Collectors.toList());
    }

    public static Double getSentimentScoreFromPersonalFeedback(FeedbackRequestDTO f) {
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

    public static Double getFeedbackRating(Feedback f) {
        return FeedbackUtil.getFeedbackRating((FeedbackScore) f);
    }

    public static Double getFeedbackRating(FeedbackRequestDTO f) {
        return getFeedbackRating((FeedbackScore) f);
    }

    public static Double getFeedbackRating(FeedbackScore f) {
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

    /**
     * Set of stopWords for use in the word cloud generation as a preprocessing step after tokenization
     * word list imported from: <a href="https://gist.github.com/sebleier/554280">https://gist.github.com/sebleier/554280</a>
     * and <a href="https://github.com/solariz/german_stopwords/blob/master/german_stopwords_plain.txt">https://github.com/solariz/german_stopwords/blob/master/german_stopwords_plain.txt</a>
     */
    public static final Set<String> stopWords = Set.of(
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your",
            "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she",
            "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
            "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "is", "are", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing", "a",
            "the", "but", "if", "or", "because", "as", "until", "while", "of",
            "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "down",
            "out", "on", "off", "over", "under", "again", "further", "then",
            "once", "here", "there", "when", "where", "why", "how", "all", "any",
            "both", "each", "few", "more", "most", "other", "some", "such", "no",
            "nor", "not", "only", "own", "same", "than", "too", "very", "s",
            "t", "can", "just", "don", "should", "now", "ab", "aber", "alle", "allein", "allem", "allen", "aller", "allerdings", "allerlei", "alles", "allmählich",
            "allzu", "als", "alsbald", "also", "am", "an", "and", "ander", "andere", "anderem", "anderen", "anderer",
            "andererseits", "anderes", "anderm", "andern", "andernfalls", "anders", "anstatt", "auch", "auf", "aus",
            "ausgenommen", "ausser", "ausserdem", "außer", "außerdem", "außerhalb", "bald", "bei", "beide", "beiden",
            "beiderlei", "beides", "beim", "beinahe", "bereits", "besonders", "besser", "beträchtlich", "bevor",
            "bezüglich", "bin", "bis", "bisher", "bislang", "bist", "bloß", "bsp.", "bzw", "ca", "ca.", "content", "da",
            "dabei", "dadurch", "dafür", "dagegen", "daher", "dahin", "damals", "damit", "danach", "daneben", "dann",
            "daran", "darauf", "daraus", "darin", "darum", "darunter", "darüber", "darüberhinaus", "das", "dass",
            "dasselbe", "davon", "davor", "dazu", "daß", "dein", "deine", "deinem", "deinen", "deiner", "deines", "dem",
            "demnach", "demselben", "den", "denen", "denn", "dennoch", "denselben", "der", "derart", "derartig", "derem",
            "deren", "derer", "derjenige", "derjenigen", "derselbe", "derselben", "derzeit", "des", "deshalb",
            "desselben", "dessen", "desto", "deswegen", "dich", "die", "diejenige", "dies", "diese", "dieselbe", "dieselben",
            "diesem", "diesen", "dieser", "dieses", "diesseits", "dir", "direkt", "direkte", "direkten", "direkter",
            "doch", "dort", "dorther", "dorthin", "drauf", "drin", "drunter", "drüber", "du", "dunklen", "durch", "durchaus",
            "eben", "ebenfalls", "ebenso", "eher", "eigenen", "eigenes", "eigentlich", "ein", "eine", "einem", "einen",
            "einer", "einerseits", "eines", "einfach", "einführen", "einführte", "einführten", "eingesetzt", "einig",
            "einige", "einigem", "einigen", "einiger", "einigermaßen", "einiges", "einmal", "eins", "einseitig", "einseitige",
            "einseitigen", "einseitiger", "einst", "einstmals", "einzig", "entsprechend", "entweder", "er", "erst", "es",
            "etc", "etliche", "etwa", "etwas", "euch", "euer", "eure", "eurem", "euren", "eurer", "eures", "falls", "fast",
            "ferner", "folgende", "folgenden", "folgender", "folgendes", "folglich", "fuer", "für", "gab", "ganze",
            "ganzem", "ganzen", "ganzer", "ganzes", "gar", "gegen", "gemäss", "ggf", "gleich", "gleichwohl", "gleichzeitig",
            "glücklicherweise", "gänzlich", "hab", "habe", "haben", "haette", "hast", "hat", "hatte", "hatten", "hattest",
            "hattet", "heraus", "herein", "hier", "hinter", "hiermit", "hiesige", "hin", "hinein", "hinten",
            "hinterher", "http", "hätt", "hätte", "hätten", "höchstens", "ich", "igitt", "ihm", "ihn", "ihnen", "ihr",
            "ihre", "ihrem", "ihren", "ihrer", "ihres", "im", "immer", "immerhin", "in", "indem", "indessen", "infolge",
            "innen", "innerhalb", "ins", "insofern", "inzwischen", "irgend", "irgendeine", "irgendwas", "irgendwen", "irgendwer",
            "irgendwie", "irgendwo", "ist", "ja", "je", "jed", "jede", "jedem", "jeden", "jedenfalls", "jeder", "jederlei",
            "jedes", "jedoch", "jemand", "jene", "jenem", "jenen", "jener", "jenes", "jenseits", "jetzt", "jährig", "jährige",
            "jährigen", "jähriges", "kam", "kann", "kannst", "kaum", "kein", "keine", "keinem", "keinen", "keiner",
            "keinerlei", "keines", "keineswegs", "klar", "klare", "klaren", "klares", "klein", "kleinen", "kleiner", "kleines",
            "koennen", "koennt", "koennte", "koennten", "komme", "kommen", "kommt", "konkret", "konkrete", "konkreten",
            "konkreter", "konkretes", "können", "könnt", "künftig", "leider", "machen", "man", "manche", "manchem", "manchen",
            "mancher", "mancherorts", "manches", "manchmal", "mehr", "mehrere", "mein", "meine", "meinem", "meinen", "meiner",
            "meines", "mich", "mir", "mit", "mithin", "muessen", "muesst", "muesste", "muss", "musst", "musste", "mussten",
            "muß", "mußt", "müssen", "müsste", "müssten", "müßt", "müßte", "nach", "nachdem", "nachher", "nachhinein", "nahm",
            "natürlich", "neben", "nebenan", "nehmen", "nein", "nicht", "nichts", "nie", "niemals", "niemand", "nirgends",
            "nirgendwo", "noch", "nun", "nur", "nächste", "nämlich", "nötigenfalls", "ob", "oben", "oberhalb", "obgleich",
            "obschon", "obwohl", "oder", "oft", "per", "plötzlich", "schließlich", "schon", "sehr", "sehrwohl", "seid", "sein",
            "seine", "seinem", "seinen", "seiner", "seines", "seit", "seitdem", "seither", "selber", "selbst", "sich", "sicher",
            "sicherlich", "sie", "sind", "so", "sobald", "sodass", "sodaß", "soeben", "sofern", "sofort", "sogar", "solange",
            "solch", "solche", "solchem", "solchen", "solcher", "solches", "soll", "sollen", "sollst", "sollt", "sollte",
            "sollten", "solltest", "somit", "sondern", "sonst", "sonstwo", "sooft", "soviel", "soweit", "sowie", "sowohl",
            "tatsächlich", "tatsächlichen", "tatsächlicher", "tatsächliches", "trotzdem", "ueber", "um", "umso", "unbedingt",
            "und", "unmöglich", "unmögliche", "unmöglichen", "unmöglicher", "uns", "unser", "unsere",
            "unserem", "unseren", "unserer", "unseres", "unter", "usw", "viel", "viele", "vielen", "vieler", "vieles",
            "vielleicht", "vielmals", "vom", "von", "vor", "voran", "vorher", "vorüber", "völlig", "wann", "war", "waren",
            "warst", "warum", "was", "weder", "weil", "weiter", "weitere", "weiterem", "weiteren", "weiterer", "weiteres",
            "weiterhin", "weiß", "welche", "welchem", "welchen", "welcher", "welches", "wem", "wen", "wenig", "wenige",
            "weniger", "wenigstens", "wenn", "wenngleich", "wer", "werde", "werden", "werdet", "weshalb", "wessen",
            "wichtig", "wie", "wieder", "wieso", "wieviel", "wiewohl", "will", "willst", "wir", "wird", "wirklich", "wirst",
            "wo", "wodurch", "wogegen", "woher", "wohin", "wohingegen", "wohl", "wohlweislich", "womit", "woraufhin", "woraus",
            "worin", "wurde", "wurden", "während", "währenddessen", " wär", "wäre", "wären", "würde", "würden", "z.B.",
            "zB", "zahlreich", "zeitweise", "zu", "zudem", "zuerst", "zufolge", "zugleich", "zuletzt", "zum", "zumal", "zur",
            "zurück", "zusammen", "zuviel", "zwar", "zwischen", "ähnlich", "übel", "über", "überall", "überallhin", "überdies",
            "übermorgen", "übrig", "übrigens"
    );
}
