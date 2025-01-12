package com.sopra.eaplanner.trainerprofile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
public class TrainerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    private Double averageRating;

    @ElementCollection
    private List<String> expertiseTags = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> hostedEvents = new ArrayList<Event>();

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonBackReference
    private User user;

    @ElementCollection
    @MapKeyColumn(name = "comment_type")
    @Column(name = "comment")
    private Map<String, String> pinnedComments = new HashMap<>();

    @OneToMany(mappedBy = "trainerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Feedback> feedbacks = new HashSet<>();

    public TrainerProfile() {
    }

    public TrainerProfile(Long id, String bio, Double averageRating, List<String> expertiseTags) {
        this.id = id;
        this.bio = bio;
        this.averageRating = averageRating;
        this.expertiseTags = expertiseTags;
    }

    public TrainerProfile(TrainerProfileRequestDTO trainerProfileRequest) {
        this.bio = trainerProfileRequest.getBio();
        this.averageRating = trainerProfileRequest.getAverageRating();
        this.expertiseTags = trainerProfileRequest.getExpertiseTags();
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

    public List<String> getExpertiseTags() {
        return expertiseTags;
    }

    public void setExpertiseTags(List<String> expertiseTags) {
        this.expertiseTags = expertiseTags;
    }

    public Map<String, String> getPinnedComments() {
        return pinnedComments;
    }

    public void setPinnedComments(Map<String, String> pinnedComments) {
        this.pinnedComments = pinnedComments;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
