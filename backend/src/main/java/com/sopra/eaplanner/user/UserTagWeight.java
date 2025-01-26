package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.tags.Tag;
import jakarta.persistence.*;

@Entity
public class UserTagWeight {

    private static final Double VISITED_EVENT_WEIGHT = 0.2;
    private static final Double GIVEN_FEEDBACK_WEIGHT = 0.3;
    private static final Double FEEDBACK_SENTIMENT_WEIGHT = 0.4;
    private static final Double FEEDBACK_RATING_WEIGHT = 0.1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    private Double tagWeight = 0.0;

    private Integer visitedEventsWithTag = 0;

    private Integer givenFeedbackWithTag = 0;

    private Double givenFeedbackResponseSentiment = 0.0;

    private Double givenFeedbackRating = 0.0;

    public UserTagWeight() {
    }

    public UserTagWeight(User user, Tag tag) {
        this.user = user;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Double getTagWeight() {
        return tagWeight;
    }

    /**
     * We calculate the tagWeight of this specified tag for the user
     * by defining a weight and using the tracked data of the user.
     */
    public void setTagWeight() {
        this.tagWeight = VISITED_EVENT_WEIGHT * visitedEventsWithTag
                + GIVEN_FEEDBACK_WEIGHT * givenFeedbackWithTag
                + FEEDBACK_SENTIMENT_WEIGHT * givenFeedbackResponseSentiment
                + FEEDBACK_RATING_WEIGHT * givenFeedbackRating;
    }

    public Integer getVisitedEventsWithTag() {
        return visitedEventsWithTag;
    }

    public void setVisitedEventsWithTag(Integer visitedEventsWithTag) {
        this.visitedEventsWithTag = visitedEventsWithTag;
        setTagWeight();
    }

    public Integer getGivenFeedbackWithTag() {
        return givenFeedbackWithTag;
    }

    public void setGivenFeedbackWithTag(Integer givenFeedbackWithTag) {
        this.givenFeedbackWithTag = givenFeedbackWithTag;
        setTagWeight();
    }

    public Double getGivenFeedbackResponseSentiment() {
        return givenFeedbackResponseSentiment;
    }

    public void setGivenFeedbackResponseSentiment(Double givenFeedbackResponseSentiment) {
        this.givenFeedbackResponseSentiment = givenFeedbackResponseSentiment;
        setTagWeight();
    }

    public Double getGivenFeedbackRating() {
        return givenFeedbackRating;
    }

    public void setGivenFeedbackRating(Double givenFeedbackRating) {
        this.givenFeedbackRating = givenFeedbackRating;
        setTagWeight();
    }

}
