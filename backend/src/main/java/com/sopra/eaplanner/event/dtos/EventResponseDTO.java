package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventResponseDTO {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String room;
    private String description;

    public EventResponseDTO() {
    }

    public EventResponseDTO(Long id, Event event) {
        this.id = id;
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
    }

    public EventResponseDTO(Event event){
        this.id = event.getId();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
    }

    public Long getId() {
        return id;
    }

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


    public static EventResponseDTO mockWith(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description) {
        return new EventResponseDTO(id, date, startTime, endTime, name, room, description);
    }

    private EventResponseDTO(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, String name, String room, String description) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.room = room;
        this.description = description;
    }
}

