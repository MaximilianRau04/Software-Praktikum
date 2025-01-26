package com.sopra.eaplanner.resource.resourceAssignment.dtos;

import java.time.LocalTime;

public class ResourceAssignmentRequest {
    private Long resourceId;
    private Long eventId;
    private Integer quantity;

    public ResourceAssignmentRequest() {
    }

    public ResourceAssignmentRequest(Long resourceId, Long eventId, Integer quantity) {
        this.resourceId = resourceId;
        this.eventId = eventId;
        this.quantity = quantity;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
