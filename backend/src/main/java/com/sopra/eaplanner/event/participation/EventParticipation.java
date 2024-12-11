package com.sopra.eaplanner.event.participation;

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
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private boolean confirmed;

    private LocalDateTime confirmationTime;

    public EventParticipation() {
    }

    public EventParticipation(User user, Event event, boolean confirmed, LocalDateTime confirmationTime) {
        this.user = user;
        this.event = event;
        this.confirmed = confirmed;
        this.confirmationTime = confirmationTime;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }

    public void setConfirmationTime(LocalDateTime confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

}
