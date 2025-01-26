package com.sopra.eaplanner.feedback.summary;

import com.sopra.eaplanner.feedback.FeedbackType;

import java.util.List;
import java.util.Map;

public class FeedbackSummaryDTO {

    private Long eventId;
    private String eventName;
    private String organizerName;

    private Map<FeedbackType, FeedbackStatistics> numericalFeedback;

    private Map<CommentType, List<CommentAnalysis>> comments;

    public FeedbackSummaryDTO() {
    }

    public FeedbackSummaryDTO(Long eventId, String eventName, String organizerName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.organizerName = organizerName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public Map<FeedbackType, FeedbackStatistics> getNumericalFeedback() {
        return numericalFeedback;
    }

    public void setNumericalFeedback(Map<FeedbackType, FeedbackStatistics> numericalFeedback) {
        this.numericalFeedback = numericalFeedback;
    }

    public Map<CommentType, List<CommentAnalysis>> getComments() {
        return comments;
    }

    public void setComments(Map<CommentType, List<CommentAnalysis>> comments) {
        this.comments = comments;
    }

    public static FeedbackSummaryDTO mockWith(Long eventId, String eventName, String organizerName) {
        return new FeedbackSummaryDTO(eventId, eventName, organizerName);
    }

    public static class FeedbackStatistics {

        private double average;
        private double median;
        private int responseCount;

        private Map<String, Double> subAverages;

        public FeedbackStatistics(double average, double median, int responseCount, Map<String, Double> subAverages) {
            this.average = average;
            this.median = median;
            this.responseCount = responseCount;
            this.subAverages = subAverages;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public double getMedian() {
            return median;
        }

        public void setMedian(double median) {
            this.median = median;
        }

        public int getResponseCount() {
            return responseCount;
        }

        public void setResponseCount(int responseCount) {
            this.responseCount = responseCount;
        }

        public Map<String, Double> getSubAverages() { return subAverages; }

        public void setSubAverages(Map<String, Double> subAverages) { this.subAverages = subAverages; }
    }

    public static class CommentAnalysis {
        private String comment;
        private String sentiment;
        private Long feedbackId;
        private String author;

        public CommentAnalysis(String comment, String sentiment, Long feedbackId, String author) {
            this.comment = comment;
            this.sentiment = sentiment;
            this.feedbackId = feedbackId;
            this.author = author;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }

        public Long getFeedbackId() {
            return feedbackId;
        }

        public void setFeedbackId(Long feedbackId) {
            this.feedbackId = feedbackId;
        }

        public String getAuthor(){
            return author;
        }

        public void setAuthor(String author){
            this.author = author;
        }
    }
}

