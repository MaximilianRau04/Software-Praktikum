package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String room;
    private String description;
    private Long exchangeDayId;
    private Long organizerId;
    private List<Long> registeredUserIds = new ArrayList<>();

    public EventDTO() {}

    public EventDTO(Long id, Event event, List<Long> registeredUserIds) {
        this.id = id;
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.room = event.getRoom();
        this.description = event.getDescription();
        this.exchangeDayId = event.getExchangeDay().getId();
        this.organizerId = event.getOrganizer().getId();
        this.registeredUserIds = registeredUserIds;
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

    public Long getExchangeDayId() {
        return exchangeDayId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public List<Long> getRegisteredUserIds() {
        return registeredUserIds;
    }
}

