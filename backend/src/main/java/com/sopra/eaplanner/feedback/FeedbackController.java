package com.sopra.eaplanner.feedback;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedbacks")
    public Iterable<Event> getAllFeedbacks() {
        return null;
    }

    @GetMapping("/feedbacks/{id}")
    public EventDTO getFeedbackById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/feedbacks")
    public Event createFeedback(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/feedbacks/{id}")
    public void deleteFeedback(@PathVariable Long id) {
        return;
    }
}
