package com.sopra.eaplanner.feedback.summary;

import java.util.List;
import java.util.Map;

public class FeedbackSummaryDTO {

    private Long eventId;
    private String eventName;
    private String organizerName;

    private Map<String, FeedbackStatistics> numericalFeedback;

    private List<String> commonWords;

    private Map<CommentType, List<CommentAnalysis>> comments;

    public FeedbackSummaryDTO() {}

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

    public Map<String, FeedbackStatistics> getNumericalFeedback() {
        return numericalFeedback;
    }

    public void setNumericalFeedback(Map<String, FeedbackStatistics> numericalFeedback) {
        this.numericalFeedback = numericalFeedback;
    }

    public List<String> getCommonWords() {
        return commonWords;
    }

    public void setCommonWords(List<String> commonWords) {
        this.commonWords = commonWords;
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


    public static class FeedbackStatistics{
        private double average;
        private double median;
        private int responseCount;

        public FeedbackStatistics(double average, double median, int responseCount) {
            this.average = average;
            this.median = median;
            this.responseCount = responseCount;
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
    }

    public static class CommentAnalysis {
        private String comment;
        private String sentiment;
        private Long feedbackId;

        public CommentAnalysis(String comment, String sentiment, Long feedbackId){
            this.comment = comment;
            this.sentiment = sentiment;
            this.feedbackId = feedbackId;
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
    }
}

