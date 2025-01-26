package com.sopra.eaplanner.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.participation.EventParticipation;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.forumpost.ForumPost;
import com.sopra.eaplanner.reward.Reward;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+", message = "Username must be alphanumeric")
    private String username;

    @Size(max = 50, message = "Firstname cannot exceed 50 characters")
    private String firstname;

    @Size(max = 50, message = "Lastname cannot exceed 50 characters")
    private String lastname;

    private String description;

    public enum Role {
        ADMIN, USER
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must be specified")
    private Role role;

    @ManyToMany(mappedBy = "registeredUsers")
    @JsonBackReference
    private Set<Event> registeredEvents = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Feedback> feedbacks = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<ForumPost> forumPosts = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private TrainerProfile trainerProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reward> rewards = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EventParticipation> participations = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTagWeight> tagWeights = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_tag",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> interestTags = new HashSet<>();

    public User() {
    }

    public User(UserRequestDTO user) {
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.role = user.getRole();
        this.description = user.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(Set<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public Set<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(Set<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }

    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }

    public Set<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Set<Reward> rewards) {
        this.rewards = rewards;
    }

    public Set<EventParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(Set<EventParticipation> participations) {
        this.participations = participations;
    }

    public Set<UserTagWeight> getTagWeights() {
        return tagWeights;
    }

    public void setTagWeights(Set<UserTagWeight> tagWeights) {
        this.tagWeights = tagWeights;
    }

    public Set<Tag> getInterestTags() {
        return interestTags;
    }

    public void setInterestTagsTags(Set<Tag> interestTags) {
        this.interestTags = interestTags;
    }

    public void updateInformation(User user) {
        if (user.getFirstname() != null) setFirstname(user.getFirstname());
        if (user.getLastname() != null) setLastname(user.getLastname());
        if (user.getRole() != null) setRole(user.getRole());
    }
}