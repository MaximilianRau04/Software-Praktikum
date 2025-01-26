package com.sopra.eaplanner.resource.resourceAssignment.dtos;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.resource.ResourceItem;
import com.sopra.eaplanner.resource.resourceAssignment.ResourceAssignment;

import java.time.LocalTime;

public class ResourceAssignmentResponse {

    private Long id;
    private ResourceItem resource;
    private EventResponseDTO event;
    private Integer quantity;

    public ResourceAssignmentResponse() {
    }

    public ResourceAssignmentResponse(Long id, ResourceItem resource, EventResponseDTO event, Integer quantity) {
        this.id = id;
        this.resource = resource;
        this.event = event;
        this.quantity = quantity;
    }

    public ResourceAssignmentResponse(ResourceAssignment resourceAssignment) {
        this.id = resourceAssignment.getId();
        this.resource = resourceAssignment.getResource();
        this.event = new EventResponseDTO(resourceAssignment.getEvent());
        this.quantity = resourceAssignment.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceItem getResource() {
        return resource;
    }

    public void setResource(ResourceItem resource) {
        this.resource = resource;
    }

    public EventResponseDTO getEvent() {
        return event;
    }

    public void setEvent(EventResponseDTO event) {
        this.event = event;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
