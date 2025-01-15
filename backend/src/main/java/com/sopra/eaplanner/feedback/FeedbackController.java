package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("")
    public Iterable<FeedbackResponseDTO> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    @GetMapping("/{id}")
    public FeedbackResponseDTO getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    @GetMapping("/{id}/author")
    public UserResponseDTO getFeedbackAuthor(@PathVariable Long id) {
        return feedbackService.getFeedbackAuthor(id);
    }

    @PostMapping("")
    public ResponseEntity<FeedbackResponseDTO> createFeedback(@Valid @RequestBody FeedbackRequestDTO requestBody) {
        FeedbackResponseDTO savedDTO = feedbackService.createFeedback(requestBody);
        URI location = URI.create("/api/feedback/" + savedDTO.getId());
        return ResponseEntity.created(location).body(savedDTO);
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}