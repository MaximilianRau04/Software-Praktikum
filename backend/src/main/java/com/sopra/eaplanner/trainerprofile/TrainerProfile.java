package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    private Double averageRating;

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Event> hostedEvents = new ArrayList<Event>();

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public TrainerProfile() {
    }

    public TrainerProfile(String bio, Double averageRating) {
        this.bio = bio;
        this.averageRating = averageRating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Event> getHostedEvents() {
        return hostedEvents;
    }

    public void setHostedEvents(List<Event> events) {
        this.hostedEvents = events;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
