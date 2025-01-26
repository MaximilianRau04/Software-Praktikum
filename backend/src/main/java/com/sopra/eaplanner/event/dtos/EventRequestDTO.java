package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.ExperienceLevel;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for creating or updating an event.
 *
 * <p>This class is used to encapsulate the data required for an event request, ensuring
 * validation rules are applied before processing. It includes information about the event's
 * name, timing, date, location, organizer, and additional metadata.</p>
 *
 * <p>Validation annotations are used to enforce constraints, such as ensuring non-null
 * fields, length limits for strings, and a future date for the event.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   EventRequestDTO request = new EventRequestDTO();
 *   request.setName("Workshop");
 *   request.setDate(LocalDate.of(2025, 1, 1));
 *   ...
 * </pre>
 */
public class EventRequestDTO {

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Start time must be set")
    private LocalTime startTime;

    @NotNull(message = "End time must be set")
    private LocalTime endTime;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @FutureOrPresent(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @Size(max = 50, message = "Room name cannot exceed 50 characters")
    private String room;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Exchange day must be specified")
    private Long exchangeDayId;

    @NotNull(message = "Organizer must be specified")
    private Long organizerId;

    @NotNull(message = "Recommended ExperienceLevel must be specified")
    private ExperienceLevel recommendedExperience;

    private Set<String> tags = new HashSet<>();

    public EventRequestDTO() {
    }

    public void setOrganizerId(@NotNull(message = "Organizer must be specified") Long organizerId) {
        this.organizerId = organizerId;
    }

    public @NotNull(message = "Exchange day must be specified") Long getExchangeDayId() {
        return exchangeDayId;
    }

    public void setExchangeDayId(@NotNull(message = "Exchange day must be specified") Long exchangeDayId) {
        this.exchangeDayId = exchangeDayId;
    }

    public @Size(max = 255, message = "Description cannot exceed 255 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 255, message = "Description cannot exceed 255 characters") String description) {
        this.description = description;
    }

    public @Size(max = 50, message = "Room name cannot exceed 50 characters") String getRoom() {
        return room;
    }

    public void setRoom(@Size(max = 50, message = "Room name cannot exceed 50 characters") String room) {
        this.room = room;
    }

    public @NotNull(message = "End time must be set") LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(@NotNull(message = "End time must be set") LocalTime endTime) {
        this.endTime = endTime;
    }

    public @NotNull(message = "Start time must be set") LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(@NotNull(message = "Start time must be set") LocalTime startTime) {
        this.startTime = startTime;
    }

    public @FutureOrPresent(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate getDate() {
        return date;
    }

    public void setDate(@FutureOrPresent(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate date) {
        this.date = date;
    }

    public @NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String name) {
        this.name = name;
    }

    public @NotNull(message = "Organizer must be specified") Long getOrganizerId() {
        return organizerId;
    }

    public ExperienceLevel getRecommendedExperience() {
        return recommendedExperience;
    }

    public void setRecommendedExperience(ExperienceLevel recommendedExperience) {
        this.recommendedExperience = recommendedExperience;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static EventRequestDTO mockWith(String name, LocalDate date, LocalTime startTime, LocalTime endTime, String description, String room, Long exchangeDayId, Long organizerId, ExperienceLevel recommendedExperience, Set<String> tags) {
        return new EventRequestDTO(name, date, startTime, endTime, room, description, exchangeDayId, organizerId, recommendedExperience, tags);
    }

    private EventRequestDTO(String name, LocalDate date, LocalTime startTime, LocalTime endTime, String room, String description, Long exchangeDayId, Long organizerId, ExperienceLevel recommendedExperience, Set<String> tags) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.description = description;
        this.exchangeDayId = exchangeDayId;
        this.organizerId = organizerId;
        this.recommendedExperience = recommendedExperience;
        this.tags = tags;
    }
}
