package com.sopra.eaplanner.resource.resourceAssignment;

import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.resources.EventResource;
import com.sopra.eaplanner.event.resources.EventResourceRepository;
import com.sopra.eaplanner.resource.ResourceItem;
import com.sopra.eaplanner.resource.ResourceRepository;
import com.sopra.eaplanner.resource.resourceAssignment.dtos.ResourceAssignmentRequest;
import com.sopra.eaplanner.resource.resourceAssignment.dtos.ResourceAssignmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.sopra.eaplanner.event.Event;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceAssignmentService {

    @Autowired
    private ResourceAssignmentRepository assignmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EventResourceRepository eventResourceRepository;

    public ResponseEntity<String> assignResource(ResourceAssignmentRequest assignmentRequest) {
        ResourceItem resource = resourceRepository.findById(assignmentRequest.getResourceId())
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        Event event = eventRepository.findById(assignmentRequest.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (assignmentRequest.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Die Menge muss größer als 0 sein.");
        }

        synchronized (this) {
            if (resource.getCapacity() < assignmentRequest.getQuantity()) {
                return ResponseEntity.badRequest().body("Nicht genügend Kapazität verfügbar.");
            }

            EventResource existingAssignment = eventResourceRepository
                    .findFirstByEventAndResource(event, resource)
                    .orElse(null);

            if (existingAssignment != null) {
                int newQuantity = existingAssignment.getQuantity() + assignmentRequest.getQuantity();

                if (newQuantity > resource.getCapacity()) {
                    return ResponseEntity.badRequest().body("Nicht genügend Kapazität verfügbar.");
                }

                existingAssignment.setQuantity(newQuantity);
                eventResourceRepository.save(existingAssignment);
            } else {
                EventResource assignment = new EventResource();
                assignment.setResource(resource);
                assignment.setEvent(event);
                assignment.setQuantity(assignmentRequest.getQuantity());
                eventResourceRepository.save(assignment);
            }

            resource.setCapacity(resource.getCapacity() - assignmentRequest.getQuantity());
            resourceRepository.save(resource);
        }

        return ResponseEntity.ok("Ressource erfolgreich zugewiesen.");
    }

}
