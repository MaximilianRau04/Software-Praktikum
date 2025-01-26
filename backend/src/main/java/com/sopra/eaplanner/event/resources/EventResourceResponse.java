package com.sopra.eaplanner.event.resources;

import com.sopra.eaplanner.resource.ResourceItem;

public class EventResourceResponse {
    private Long id;
    private ResourceItem resource;
    private int quantity;

    public EventResourceResponse(Long id, ResourceItem resource, int quantity) {
        this.id = id;
        this.resource = resource;
        this.quantity = quantity;
    }

    public EventResourceResponse() {}

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
