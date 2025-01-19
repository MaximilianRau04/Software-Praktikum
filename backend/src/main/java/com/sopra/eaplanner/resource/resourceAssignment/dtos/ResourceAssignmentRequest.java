package com.sopra.eaplanner.resource.resourceAssignment.dtos;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.resource.ResourceItem;

import java.time.LocalTime;

public class ResourceAssignmentRequest {
    private Long resourceId;
    private Long eventId;
    private LocalTime startTime;
    private LocalTime endTime;

    public ResourceAssignmentRequest() {
    }

    public ResourceAssignmentRequest(Long resourceId, Long eventId, LocalTime startTime, LocalTime endTime) {
        this.resourceId = resourceId;
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResource(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
