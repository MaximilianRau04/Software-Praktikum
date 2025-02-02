package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class ParticipatedEventCollectionDTO {

    List<EventResponseDTO> registeredEvents = new ArrayList<>();

    List<EventResponseDTO> confirmedEvents = new ArrayList<>();

    List<EventResponseDTO> recommendedEvents = new ArrayList<>();

    List<EventResponseDTO> completedEvents = new ArrayList<>();

    public ParticipatedEventCollectionDTO(List<EventResponseDTO> registered, List<EventResponseDTO> confirmed, List<EventResponseDTO> completed, List<EventResponseDTO> recommended) {
        this.registeredEvents = registered;
        this.confirmedEvents = confirmed;
        this.recommendedEvents = recommended;
        this.completedEvents = completed;
    }

    public List<EventResponseDTO> getRegisteredEvents() {
        return registeredEvents;
    }
    public void setRegisteredEvents(List<EventResponseDTO> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }
    public List<EventResponseDTO> getConfirmedEvents() {
        return confirmedEvents;
    }
    public void setConfirmedEvents(List<EventResponseDTO> confirmedEvents) {
        this.confirmedEvents = confirmedEvents;
    }
    public List<EventResponseDTO> getRecommendedEvents() {
        return recommendedEvents;
    }
    public void setRecommendedEvents(List<EventResponseDTO> recommendedEvents) {
        this.recommendedEvents = recommendedEvents;
    }
    public List<EventResponseDTO> getCompletedEvents() {
        return completedEvents;
    }
    public void setCompletedEvents(List<EventResponseDTO> completedEvents) {
        this.completedEvents = completedEvents;
    }
}
