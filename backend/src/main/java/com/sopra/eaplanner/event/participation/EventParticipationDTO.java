package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;

import java.time.LocalDateTime;

public class EventParticipationDTO {

    private Long id;
    private UserResponseDTO user;
    private EventResponseDTO event;

    private boolean confirmed;

    private LocalDateTime confirmationTime;

    public EventParticipationDTO(EventParticipation eventParticipation) {
        this.id = eventParticipation.getId();
        this.user = new UserResponseDTO(eventParticipation.getUser());
        this.event = new EventResponseDTO(eventParticipation.getEvent());
        this.confirmed = eventParticipation.isConfirmed();
        this.confirmationTime = eventParticipation.getConfirmationTime();
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

    public boolean getIsConfirmed() {
        return confirmed;
    }

    public LocalDateTime getConfirmationTime() {
        return confirmationTime;
    }
}
