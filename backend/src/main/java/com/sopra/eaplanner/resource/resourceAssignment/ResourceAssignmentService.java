package com.sopra.eaplanner.resource.resourceAssignment;

import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.resource.ResourceItem;
import com.sopra.eaplanner.resource.ResourceRepository;
import com.sopra.eaplanner.resource.resourceAssignment.dtos.ResourceAssignmentRequest;
import com.sopra.eaplanner.resource.resourceAssignment.dtos.ResourceAssignmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.sopra.eaplanner.event.Event;

import java.util.List;

@Service
public class ResourceAssignmentService {

    @Autowired
    private ResourceAssignmentRepository assignmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    public ResponseEntity<?> assignResource(ResourceAssignmentRequest assignmentRequest) {

        Event event = eventRepository.findById(assignmentRequest.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<ResourceAssignment> conflicts = assignmentRepository.findConflictingAssignments(
                assignmentRequest.getResourceId(),
                assignmentRequest.getEventId(),
                assignmentRequest.getStartTime(),
                assignmentRequest.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Resource is already assigned during this time slot.");
        }

        ResourceItem resourceItem = resourceRepository.findById(assignmentRequest.getResourceId())
                .orElseThrow(() -> new RuntimeException("ResourceItem not found"));

        event.addResource(resourceItem);
        eventRepository.save(event);

        ResourceAssignment assignment = new ResourceAssignment();
        assignment.setResource(resourceItem);
        assignment.setEvent(event);
        assignment.setStartTime(assignmentRequest.getStartTime());
        assignment.setEndTime(assignmentRequest.getEndTime());

        assignmentRepository.save(assignment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResourceAssignmentResponse(assignment));
    }
}
