package com.sopra.eaplanner.trainerprofile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.tags.Tag;
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

    private Integer feedbackCount;

    @ManyToMany
    @JoinTable(
            name = "trainer_profile_tag",
            joinColumns = @JoinColumn(name = "trainer_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> expertiseTags = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonBackReference
    private User user;

    @ElementCollection
    @MapKeyColumn(name = "comment_type")
    @Column(name = "comment")
    private Map<String, String> pinnedComments = new HashMap<>();

    public TrainerProfile() {
    }

    public TrainerProfile(Long id, String bio, Set<Tag> expertiseTags) {
        this.id = id;
        this.bio = bio;
        this.expertiseTags = expertiseTags;
    }

    public TrainerProfile(TrainerProfileRequestDTO trainerProfileRequest, Set<Tag> expertiseTags) {
        this.bio = trainerProfileRequest.getBio();
        this.expertiseTags = expertiseTags;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Tag> getExpertiseTags() {
        return expertiseTags;
    }

    public void setExpertiseTags(Set<Tag> expertiseTags) {
        this.expertiseTags = expertiseTags;
    }

    public Map<String, String> getPinnedComments() {
        return pinnedComments;
    }

    public void setPinnedComments(Map<String, String> pinnedComments) {
        this.pinnedComments = pinnedComments;
    }

    public Integer getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(Integer feedbackCount) {
        this.feedbackCount = feedbackCount;
    }
}
