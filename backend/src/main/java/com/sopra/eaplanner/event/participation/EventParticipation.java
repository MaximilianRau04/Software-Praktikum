package com.sopra.eaplanner.event.participation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    private boolean participationConfirmed;
    private LocalDateTime confirmationTime;

    private boolean feedbackGiven;
    private LocalDateTime feedbackTime;

    public EventParticipation() {
    }

    public EventParticipation(User user, Event event, boolean participationConfirmed, LocalDateTime confirmationTime, boolean feedbackGiven, LocalDateTime feedbackTime) {
        this.user = user;
        this.event = event;
        this.participationConfirmed = participationConfirmed;
        this.confirmationTime = confirmationTime;
        this.feedbackGiven = feedbackGiven;
        this.feedbackTime = feedbackTime;
    }

    public EventParticipation(User user, Event event) {
        this.user = user;
        this.event = event;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isParticipationConfirmed() {
        return participationConfirmed;
    }

    public void setParticipationConfirmed(boolean confirmed) {
        this.participationConfirmed = confirmed;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(LocalDateTime confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public boolean isFeedbackGiven() {
        return feedbackGiven;
    }

    public void setFeedbackGiven(boolean feedbackGiven) {
        this.feedbackGiven = feedbackGiven;
    }

    public LocalDateTime getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(LocalDateTime feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

}
