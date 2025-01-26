package com.sopra.eaplanner.trainerprofile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.trainerprofile.comments.PinnedComment;
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

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonBackReference
    private User user;

    private Double averageRating = 0.0;

    private Integer feedbackCount = 0;

    @ManyToMany
    @JoinTable(
            name = "trainer_profile_tag",
            joinColumns = @JoinColumn(name = "trainer_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> expertiseTags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PinnedComment> pinnedComments = new ArrayList<>();

    public TrainerProfile() {
    }

    public TrainerProfile(TrainerProfileRequestDTO trainerProfileRequest, User user) {
        this.bio = trainerProfileRequest.getBio();
        this.user = user;
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

    public List<PinnedComment> getPinnedComments() {
        return pinnedComments;
    }

    public void setPinnedComments(List<PinnedComment> pinnedComments) {
        this.pinnedComments = pinnedComments;
    }

    public Integer getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(Integer feedbackCount) {
        this.feedbackCount = feedbackCount;
    }
}
