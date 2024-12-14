package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;

import java.time.LocalTime;

public class EventResponseDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String room;
    private String description;
    private Long organizer;

    public EventResponseDTO() {
    }

    public EventResponseDTO(Long id, Event event) {
        this.id = id;
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
        this.organizer = event.getOrganizer().getId();
    }

    public EventResponseDTO(Event event){
        this.id = event.getId();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
        this.organizer = event.getOrganizer().getId();
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Long organizer) {
        this.organizer = organizer;
    }
}

