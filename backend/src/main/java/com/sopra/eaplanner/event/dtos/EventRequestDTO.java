package com.sopra.eaplanner.event.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class EventRequestDTO {

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Start time must be set")
    private LocalTime startTime;

    @NotNull(message = "End time must be set")
    private LocalTime endTime;

    @Size(max = 50, message = "Room name cannot exceed 50 characters")
    private String room;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Exchange day must be specified")
    private Long exchangeDayId;

    @NotNull(message = "Organizer must be specified")
    private Long organizerId;

    @NotNull(message = "TrainerProfile must be specified")
    private Long trainerProfileId;

    public EventRequestDTO(){
    }

    public void setTrainerProfileId(Long trainerProfileId) {
        this.trainerProfileId = trainerProfileId;
    }

    public void setOrganizerId(@NotNull(message = "Organizer must be specified") Long organizerId) {
        this.organizerId = organizerId;
    }

    public @NotNull(message = "Exchange day must be specified") Long getExchangeDayId() {
        return exchangeDayId;
    }

    public void setExchangeDayId(@NotNull(message = "Exchange day must be specified") Long exchangeDayId) {
        this.exchangeDayId = exchangeDayId;
    }

    public @Size(max = 255, message = "Description cannot exceed 255 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 255, message = "Description cannot exceed 255 characters") String description) {
        this.description = description;
    }

    public @Size(max = 50, message = "Room name cannot exceed 50 characters") String getRoom() {
        return room;
    }

    public void setRoom(@Size(max = 50, message = "Room name cannot exceed 50 characters") String room) {
        this.room = room;
    }

    public @NotNull(message = "End time must be set") LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(@NotNull(message = "End time must be set") LocalTime endTime) {
        this.endTime = endTime;
    }

    public @NotNull(message = "Start time must be set") LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(@NotNull(message = "Start time must be set") LocalTime startTime) {
        this.startTime = startTime;
    }

    public @NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String name) {
        this.name = name;
    }

    public @NotNull(message = "Organizer must be specified") Long getOrganizerId() {
        return organizerId;
    }

    public Long getTrainerProfileId() { return trainerProfileId; }

    public static EventRequestDTO mockWith(String name, LocalTime startTime, LocalTime endTime, String description, String room, Long exchangeDayId, Long organizerId, Long trainerProfileId) {
        return new EventRequestDTO(name, startTime, endTime, room, description, exchangeDayId, organizerId, trainerProfileId);
    }

    private EventRequestDTO(String name, LocalTime startTime, LocalTime endTime, String room, String description, Long exchangeDayId, Long organizerId, Long trainerProfileId) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.description = description;
        this.exchangeDayId = exchangeDayId;
        this.organizerId = organizerId;
        this.trainerProfileId = trainerProfileId;
    }
}
