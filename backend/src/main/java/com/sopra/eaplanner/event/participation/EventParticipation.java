package com.sopra.eaplanner.event.participation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing the participation of a user in an event.
 *
 * <p>This class models the relationship between a {@link User} and an {@link Event}, capturing
 * details about the user's participation status, confirmation of attendance, and feedback.
 * It serves as a link table with additional metadata.</p>
 *
 * <p>Key fields include:</p>
 * <ul>
 *   <li>{@code isParticipationConfirmed} - Indicates whether the user has confirmed their attendance.</li>
 *   <li>{@code confirmationTime} - Records the timestamp of the attendance confirmation.</li>
 *   <li>{@code feedbackGiven} - Tracks whether feedback has been submitted by the user.</li>
 *   <li>{@code feedbackTime} - Records the timestamp of the feedback submission.</li>
 * </ul>
 *
 * <p>The relationships between users and events are defined as {@code @ManyToOne} with
 * foreign key constraints, ensuring proper linkage between entities. {@code @JsonBackReference}
 * is used to prevent circular references during JSON serialization.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   EventParticipation participation = new EventParticipation();
 *   participation.setUser(user);
 *   participation.setEvent(event);
 *   participation.setParticipationConfirmed(true);
 * </pre>
 */
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

    private boolean isParticipationConfirmed;
    private LocalDateTime confirmationTime;

    private boolean feedbackGiven;
    private LocalDateTime feedbackTime;

    public EventParticipation() {
    }

    public EventParticipation(User user, Event event, boolean isParticipationConfirmed, LocalDateTime confirmationTime, boolean feedbackGiven, LocalDateTime feedbackTime) {
        this.user = user;
        this.event = event;
        this.isParticipationConfirmed = isParticipationConfirmed;
        this.confirmationTime = confirmationTime;
        this.feedbackGiven = feedbackGiven;
        this.feedbackTime = feedbackTime;
    }

    public EventParticipation(User user, Event event) {
        this.user = user;
        this.event = event;
        this.isParticipationConfirmed = false;
        this.confirmationTime = null;
        this.feedbackGiven = false;
        this.feedbackTime = null;
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

    public boolean getIsParticipationConfirmed() {
        return isParticipationConfirmed;
    }

    public void setIsParticipationConfirmed(boolean isParticipationConfirmed) {
        this.isParticipationConfirmed = isParticipationConfirmed;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(LocalDateTime confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    public boolean getFeedbackGiven() {
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
