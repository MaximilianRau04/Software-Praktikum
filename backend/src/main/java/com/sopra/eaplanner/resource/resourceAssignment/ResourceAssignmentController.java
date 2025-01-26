package com.sopra.eaplanner.resource.resourceAssignment;

import com.sopra.eaplanner.resource.resourceAssignment.dtos.ResourceAssignmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
public class ResourceAssignmentController {

    @Autowired
    private ResourceAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<String> assignResource(@RequestBody ResourceAssignmentRequest assignment) {
        return assignmentService.assignResource(assignment);
    }
}
