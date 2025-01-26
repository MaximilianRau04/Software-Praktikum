package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.ExperienceLevel;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for representing the details of an event in responses.
 *
 * <p>This class is used to encapsulate and transfer event data from the backend to the
 * frontend or client, ensuring a clear and consistent structure for responses. It includes
 * key details about the event, such as its ID, associated exchange day, timing, name, location,
 * and recommended experience level.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   EventResponseDTO response = new EventResponseDTO();
 *   response.setId(1L);
 *   response.setName("Workshop");
 *   response.setDate(LocalDate.of(2025, 1, 1));
 *   ...
 * </pre>
 */
public class EventResponseDTO {
    private Long id;
    private Long exchangeDayId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String room;
    private String description;
    private ExperienceLevel recommendedExperience;

    public EventResponseDTO() {
    }

    public EventResponseDTO(Long id, Event event) {
        this.id = id;
        this.exchangeDayId = event.getExchangeDay().getId();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
        this.recommendedExperience = event.getRecommendedExperience();
    }

    public EventResponseDTO(Event event) {
        this.id = event.getId();
        this.exchangeDayId = event.getExchangeDay().getId();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
        this.recommendedExperience = event.getRecommendedExperience();
    }

    public Long getId() {
        return id;
    }

    public Long getExchangeDayId(){return exchangeDayId;}

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public String getDescription() {
        return description;
    }

    public ExperienceLevel getRecommendedExperience() {
        return recommendedExperience;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExchangeDayId(Long exchangeDayId){this.exchangeDayId = exchangeDayId;}

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecommendedExperience(ExperienceLevel recommendedExperience) {
        this.recommendedExperience = recommendedExperience;
    }

    public static EventResponseDTO mockWith(Long id, Long exchangeDayId, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description, ExperienceLevel recommendedExperience) {
        return new EventResponseDTO(id, exchangeDayId, date, startTime, endTime, name, room, description, recommendedExperience);
    }

    private EventResponseDTO(Long id, Long exchangeDayId, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description, ExperienceLevel recommendedExperience) {
        this.id = id;
        this.exchangeDayId = exchangeDayId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.room = room;
        this.description = description;
        this.recommendedExperience = recommendedExperience;
    }
}

