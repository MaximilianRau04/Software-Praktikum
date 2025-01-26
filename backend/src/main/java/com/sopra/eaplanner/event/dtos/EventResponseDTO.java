package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.ExperienceLevel;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private Boolean inviteOnly;

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
        this.inviteOnly = event.getInviteOnly();
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
        this.inviteOnly = event.getInviteOnly();
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

    public Boolean getInviteOnly() {
        return inviteOnly;
    }

    public void setInviteOnly(Boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public static EventResponseDTO mockWith(Long id, Long exchangeDayId, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description, ExperienceLevel recommendedExperience, Boolean inviteOnly) {
        return new EventResponseDTO(id, exchangeDayId, date, startTime, endTime, name, room, description, recommendedExperience, inviteOnly);
    }

    private EventResponseDTO(Long id, Long exchangeDayId, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description, ExperienceLevel recommendedExperience, Boolean inviteOnly) {
        this.id = id;
        this.exchangeDayId = exchangeDayId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.room = room;
        this.description = description;
        this.recommendedExperience = recommendedExperience;
        this.inviteOnly = inviteOnly;
    }
}

