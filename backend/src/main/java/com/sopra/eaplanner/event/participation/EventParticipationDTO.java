package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;

import java.time.LocalDateTime;

public class EventParticipationDTO {

    private Long id;
    private UserResponseDTO user;
    private EventResponseDTO event;

    private boolean participationConfirmed;
    private LocalDateTime confirmationTime;

    private boolean feedbackGiven;
    private LocalDateTime feedbackTime;

    public EventParticipationDTO(EventParticipation eventParticipation) {
        this.id = eventParticipation.getId();
        this.user = new UserResponseDTO(eventParticipation.getUser());
        this.event = new EventResponseDTO(eventParticipation.getEvent());
        this.participationConfirmed = eventParticipation.getIsParticipationConfirmed();
        this.confirmationTime = eventParticipation.getConfirmationTime();
        this.feedbackGiven = eventParticipation.getFeedbackGiven();
        this.feedbackTime = eventParticipation.getFeedbackTime();
    }

    public Long getId() {
        return id;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public EventResponseDTO getEvent() {
        return event;
    }

    public boolean getParticipationConfirmed() {
        return participationConfirmed;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }

    public boolean getFeedbackGiven() {
        return feedbackGiven;
    }
    public LocalDateTime getFeedbackTime() {
        return feedbackTime;
    }
}
