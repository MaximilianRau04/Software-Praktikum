package com.sopra.eaplanner.event;

import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.user.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String room;
    private String description;
    private ExchangeDay exchangeDay;
    private User organizer;
    private List<Long> registeredUserIds;

    public EventDTO(Long id, LocalTime startTime, LocalTime endTime, String name,
                    String room, String description, ExchangeDay exchangeDay, User organizer,
                    List<Long> registeredUserIds) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.room = room;
        this.description = description;
        this.exchangeDay = exchangeDay;
        this.organizer = organizer;
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

    public ExchangeDay getExchangeDay() {
        return exchangeDay;
    }

    public User getOrganizer() {
        return organizer;
    }

    public List<Long> getRegisteredUserIds() {
        return registeredUserIds;
    }
}

