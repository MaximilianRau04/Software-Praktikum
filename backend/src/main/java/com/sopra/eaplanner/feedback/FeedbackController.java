package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("")
    public Iterable<Feedback> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    @GetMapping("/{id}")
    public Feedback getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    @PostMapping("")
    public FeedbackResponseDTO createFeedback(@Valid @RequestBody FeedbackRequestDTO requestBody) {
        return feedbackService.createFeedback(requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public void deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
    }
}
