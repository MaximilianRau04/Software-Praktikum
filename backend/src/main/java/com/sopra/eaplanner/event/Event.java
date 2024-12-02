package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventDTO;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Start time must be set")
    private LocalTime startTime;

    @NotNull(message = "End time must be set")
    private LocalTime endTime;

    @Size(max = 50, message = "Room name cannot exceed 50 characters")
    private String room;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Exchange day must be specified")
    @ManyToOne
    @JoinColumn(name = "exchange_day_id", nullable = false)
    @JsonBackReference
    private ExchangeDay exchangeDay;

    @NotNull(message = "Organizer must be specified")
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @ManyToOne
    @JoinColumn(name = "trainer_profile_id")
    private TrainerProfile trainerProfile;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> registeredUsers = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    public Event() {
    }

    public Event(Long id, String name, LocalTime startTime, LocalTime endTime, String room, String description, ExchangeDay exchangeDay, User organizer, TrainerProfile trainerProfile) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.description = description;
        this.exchangeDay = exchangeDay;
        this.organizer = organizer;
        this.trainerProfile = trainerProfile;
    }

    public Event(EventDTO eventDTO, ExchangeDay exchangeDay, User organizer, List<User> registeredUsers) {
        this.id = eventDTO.getId();
        this.name = eventDTO.getName();
        this.startTime = eventDTO.getStartTime();
        this.endTime = eventDTO.getEndTime();
        this.room = eventDTO.getRoom();
        this.description = eventDTO.getDescription();
        this.exchangeDay = exchangeDay;
        this.organizer = organizer;
        this.registeredUsers = registeredUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExchangeDay getExchangeDay() {
        return exchangeDay;
    }

    public void setExchangeDay(ExchangeDay exchangeDay) {
        this.exchangeDay = exchangeDay;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public TrainerProfile getTrainerProfile() {
        return trainerProfile;
    }

    public void setTrainerProfile(TrainerProfile trainerProfile) {
        this.trainerProfile = trainerProfile;
    }

    public List<User> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}
