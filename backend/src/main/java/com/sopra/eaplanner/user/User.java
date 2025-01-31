package com.sopra.eaplanner.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.auth.models.userlogin.UserLogin;
import com.sopra.eaplanner.auth.payload.request.SignupRequest;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.participation.EventParticipation;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.forumpost.ForumPost;
import com.sopra.eaplanner.reward.Reward;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_.\\-]+", message = "Username must be alphanumeric")
    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserLogin userLogin;

    @Size(max = 50, message = "Firstname cannot exceed 50 characters")
    private String firstname;

    @Size(max = 50, message = "Lastname cannot exceed 50 characters")
    private String lastname;

    private String description;

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

    public User(String username, String firstname, String lastname, String bio, Set<Tag> interestTags) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.description = bio;
        this.interestTags = interestTags;
    }

    public User(SignupRequest request, UserLogin userLogin) {
        this.username = request.getUsername();
        this.firstname = request.getFirstName();
        this.lastname = request.getLastName();
        this.description = request.getBio();
        this.userLogin = userLogin;
    }

    public User(UserRequestDTO user) {
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.description = user.getDescription();
    }
}