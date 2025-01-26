package com.sopra.eaplanner.event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.participation.EventParticipation;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.forumthread.ForumThread;
import com.sopra.eaplanner.notification.reminder.ReminderType;
import com.sopra.eaplanner.resource.ResourceItem;
import com.sopra.eaplanner.resource.resourceAssignment.ResourceAssignment;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Entity class representing an event in the system.
 *
 * <p>An event has details such as name, date, time, room, description, associated exchange day, organizer, and participants. It also supports feedback, reminders, forum threads, resources, and tags.</p>
 *
 * <p>Each event is related to a specific {@link ExchangeDay} and {@link User} (organizer). It can have multiple {@link User}s as registered participants, as well as associated {@link Tag}s for categorization.</p>
 *
 * <p>Additional features include managing participation, feedback, attendance, QR codes, and reminders.</p>
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @FutureOrPresent(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

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
    @JsonManagedReference
    private User organizer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonManagedReference
    private Set<User> registeredUsers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Feedback> feedbacks = new ArrayList<>();

    private String attendanceToken;

    private String qrCodeFilePath;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EventParticipation> participations = new ArrayList<>();

    @ElementCollection
    private Map<ReminderType, Boolean> remindersSent = new HashMap<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<ForumThread> forumThreads = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ResourceItem> resources = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceAssignment> resourceAssignments = new ArrayList<>();

    private ExperienceLevel recommendedExperience;

    @ManyToMany
    @JoinTable(
            name = "event_tag",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Event() {
    }

    public Event(Long id, String name, LocalDate date, LocalTime startTime, LocalTime endTime, String room,
                 String description, ExchangeDay exchangeDay, User organizer,
                 String qrCodeFilePath, Set<ForumThread> forumthreads, ExperienceLevel recommendedExperience) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.description = description;
        this.exchangeDay = exchangeDay;
        this.organizer = organizer;
        this.qrCodeFilePath = qrCodeFilePath;
        this.attendanceToken = generateAttendanceToken();
        this.forumThreads = forumthreads;
        this.date = date;
        this.recommendedExperience = recommendedExperience;
    }

    public Event(EventRequestDTO eventDTO, ExchangeDay exchangeDay, User organizer, Set<Tag> tags) {
        this.name = eventDTO.getName();
        this.startTime = eventDTO.getStartTime();
        this.endTime = eventDTO.getEndTime();
        this.date = eventDTO.getDate();
        this.room = eventDTO.getRoom();
        this.description = eventDTO.getDescription();
        this.exchangeDay = exchangeDay;
        this.organizer = organizer;
        this.attendanceToken = generateAttendanceToken();
        this.recommendedExperience = eventDTO.getRecommendedExperience();
        this.tags = tags;
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

    public @FutureOrPresent(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate getDate() {
        return date;
    }

    public void setDate(@FutureOrPresent(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate date) {
        this.date = date;
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

    public Set<User> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(Set<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getAttendanceToken() {
        return attendanceToken;
    }

    public void setAttendanceToken(String attendanceToken) {
        this.attendanceToken = attendanceToken;
    }

    public void setQrCodeFilePath(String qrCodeFilePath) {
        this.qrCodeFilePath = qrCodeFilePath;
    }

    public List<EventParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<EventParticipation> participations) {
        this.participations = participations;
    }

    public String getQrCodeFilePath() {
        return qrCodeFilePath;
    }

    public Map<ReminderType, Boolean> getRemindersSent() {
        return remindersSent;
    }

    public void setRemindersSent(Map<ReminderType, Boolean> remindersSent) {
        this.remindersSent = remindersSent;
    }

    @Transient
    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(exchangeDay.getStartDate(), startTime);
    }

    public List<ResourceItem> getResources() {
        return resources;
    }

    public void addResource(ResourceItem resource) {
        resources.add(resource);
    }

    public ExperienceLevel getRecommendedExperience() {
        return recommendedExperience;
    }

    public void setRecommendedExperience(ExperienceLevel recommendedExperience) {
        this.recommendedExperience = recommendedExperience;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @PostLoad
    public void initRemindersSent() {
        for (ReminderType type : ReminderType.values()) {
            remindersSent.putIfAbsent(type, false);
        }
    }

    private String generateAttendanceToken() {
        Random random = new Random();
        Random charSet = new Random();
        StringBuilder url = new StringBuilder();
        int randomPortionLength = 10;

        for (int i = 0; i < randomPortionLength; i++) {
            if (charSet.nextBoolean()) {
                url.append(random.nextInt(9));
            } else {
                url.append((char) ('a' + random.nextInt(26)));
            }
        }

        return url.toString();
    }

    public Set<ForumThread> getForumThreads() {
        return forumThreads;
    }

    public void setForumThreads(Set<ForumThread> forumThreads) {
        this.forumThreads = forumThreads;
    }

    public void setResources(List<ResourceItem> resources) {
        this.resources = resources;
    }

    public List<ResourceAssignment> getResourceAssignments() {
        return resourceAssignments;
    }

    public void setResourceAssignments(List<ResourceAssignment> resourceAssignments) {
        this.resourceAssignments = resourceAssignments;
    }
}
