package com.sopra.eaplanner.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.forumpost.ForumPost;
import com.sopra.eaplanner.reward.Reward;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

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

    public enum Role {
        ADMIN, USER
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must be specified")
    private Role role;

    @ManyToMany(targetEntity = Event.class)
    private List<Event> registeredEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumPost> forumPosts = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private TrainerProfile trainerProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reward> rewards = new ArrayList<>();

    public User() {}

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }
    public void setRegisteredEvents(List<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }
    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }
    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }
    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }
    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }
    public List<Reward> getRewards() {
        return rewards;
    }
    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public void updateInformation(User user) {
        if (user.getFirstname() != null) setFirstname(user.getFirstname());
        if (user.getLastname() != null) setLastname(user.getLastname());
        if (user.getRole() != null) setRole(user.getRole());
    }
}