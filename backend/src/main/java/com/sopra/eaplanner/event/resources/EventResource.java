package com.sopra.eaplanner.event.resources;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.resource.ResourceItem;
import jakarta.persistence.*;

    @Entity
    public class EventResource {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JsonBackReference
        @JoinColumn(name = "event_id", nullable = false)
        private Event event;

        @ManyToOne
        @JoinColumn(name = "resource_id", nullable = false)
        private ResourceItem resource;

        @Column(name = "quantity", nullable = false)
        private int quantity;

        public EventResource(Long id, ResourceItem resource, int quantity) {
            this.id = id;
            this.resource = resource;
            this.quantity = quantity;
        }

        public EventResource() {

        }

        public void setId(Long id) {
           this.id = id;
       }

       public Long getId() {
           return id;
       }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
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


