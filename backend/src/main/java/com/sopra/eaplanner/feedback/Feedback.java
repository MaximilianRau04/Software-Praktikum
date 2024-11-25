package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ValueGenerationType;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Integer score;

    private boolean anonymousFeedback;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public Feedback() {
    }

    public Feedback(String comment, Integer score, boolean anonymousFeedback, Event event, User user) {
        this.comment = comment;
        this.score = score;
        this.anonymousFeedback = anonymousFeedback;
        this.event = event;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public boolean isAnonymousFeedback() {
        return anonymousFeedback;
    }

    public void setAnonymousFeedback(boolean anonymousFeedback) {
        this.anonymousFeedback = anonymousFeedback;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
