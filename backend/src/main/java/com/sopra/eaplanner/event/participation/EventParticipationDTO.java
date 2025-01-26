package com.sopra.eaplanner.event.participation;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing the details of a user's participation in an event.
 *
 * <p>This class is used to transfer data related to an {@link EventParticipation} entity,
 * including the user, the event, and metadata about participation status, confirmation time,
 * and feedback.</p>
 *
 * <p>The DTO is constructed directly from an {@link EventParticipation} entity, ensuring a
 * streamlined conversion of entity data to a client-facing format. It encapsulates details
 * about:</p>
 * <ul>
 *   <li>The user's identity via a {@link UserResponseDTO}.</li>
 *   <li>The associated event's details via a {@link EventResponseDTO}.</li>
 *   <li>The user's confirmation status and the timestamp of the confirmation.</li>
 *   <li>Whether feedback has been provided and the timestamp of the feedback submission.</li>
 * </ul>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   EventParticipationDTO dto = new EventParticipationDTO(eventParticipation);
 *   System.out.println(dto.isParticipationConfirmed()); // Outputs: true or false
 * </pre>
 */
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
