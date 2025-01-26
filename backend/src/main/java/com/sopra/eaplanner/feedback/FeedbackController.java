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

/**
 * Controller for managing Feedback entities in the system.
 * Handles HTTP requests for creating, reading, and deleting Feedback records.
 * Provides endpoints for retrieving all feedbacks, a single feedback by its ID,
 * and the author of a specific feedback.
 *
 * <p>All methods in this controller are mapped to the "/api/feedback" URL path.</p>
 *
 * <p>Available endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/feedback} - Retrieves a list of all feedbacks.</li>
 *   <li>{@code GET /api/feedback/{id}} - Retrieves a specific feedback by its ID.</li>
 *   <li>{@code GET /api/feedback/{id}/author} - Retrieves the author of a specific feedback.</li>
 *   <li>{@code POST /api/feedback} - Creates a new feedback.</li>
 *   <li>{@code DELETE /api/feedback/{id}} - Deletes an existing feedback by its ID.</li>
 * </ul>
 *
 * <p>Each response is returned with an appropriate HTTP status code and the data requested or created.</p>
 *
 * <p>Input validation is performed on the request bodies using JSR-303 annotations.
 * The controller uses {@link FeedbackService} to interact with the business logic for feedback management.</p>
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Retrieves a list of all feedbacks.
     *
     * @return an {@link Iterable} of {@link FeedbackResponseDTO} objects representing all feedbacks
     */
    @GetMapping("")
    public Iterable<FeedbackResponseDTO> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }

    /**
     * Retrieves a specific feedback by its ID.
     *
     * @param id the ID of the feedback to retrieve
     * @return the {@link FeedbackResponseDTO} representing the feedback
     */
    @GetMapping("/{id}")
    public FeedbackResponseDTO getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    /**
     * Retrieves the author of a specific feedback.
     *
     * @param id the ID of the feedback
     * @return the {@link UserResponseDTO} representing the feedback author
     */
    @GetMapping("/{id}/author")
    public UserResponseDTO getFeedbackAuthor(@PathVariable Long id) {
        return feedbackService.getFeedbackAuthor(id);
    }

    /**
     * Creates a new feedback entry.
     * <p>
     * This endpoint expects a {@link FeedbackRequestDTO} containing the feedback data in the request body.
     * </p>
     *
     * @param requestBody the data of the feedback to be created
     * @return a {@link ResponseEntity} containing the created feedback and a {@link URI} pointing to the new feedback's resource
     */
    @PostMapping("")
    public ResponseEntity<FeedbackResponseDTO> createFeedback(@Valid @RequestBody FeedbackRequestDTO requestBody) {
        FeedbackResponseDTO savedDTO = feedbackService.createFeedback(requestBody);
        URI location = URI.create("/api/feedback/" + savedDTO.getId());
        return ResponseEntity.created(location).body(savedDTO);
    }

    /**
     * Deletes a feedback by its ID.
     *
     * @param id the ID of the feedback to delete
     * @return a {@link ResponseEntity} indicating the status of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}