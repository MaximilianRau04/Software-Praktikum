package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.event.resources.EventResource;
import com.sopra.eaplanner.event.resources.EventResourceResponse;
import com.sopra.eaplanner.event.tags.TagResponseDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.sopra.eaplanner.forumthread.ForumThreadResponseDTO;
import com.sopra.eaplanner.locations.LocationDTO;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for managing events in the system.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating, and deleting events. It also allows access to event-related data such as tags, feedback, resources, forum threads, and attendance information.</p>
 *
 * <p>Each endpoint corresponds to a specific operation related to events, such as fetching events by tags, retrieving feedback summaries, generating word clouds, or managing event resources.</p>
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Retrieves all events, or filters events based on a list of tags.
     *
     * @param tags The tags to filter events by (optional).
     * @return A list of events that match the specified tags.
     */
    @GetMapping("")
    public ResponseEntity<List<EventResponseDTO>> getEventsWithTags(@RequestParam(required = false) List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(eventService.getAllEvents());
        }
        return ResponseEntity.ok(eventService.getEventsFromTags(tags));
    }

    /**
     * Retrieves a specific event by its ID.
     *
     * @param id The ID of the event.
     * @return The details of the event.
     */
    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    /**
     * Retrieves the list of users registered for a specific event.
     *
     * @param id The ID of the event.
     * @return An iterable list of users who are registered for the event.
     */
    @GetMapping("/{id}/registeredUsers")
    public Iterable<UserResponseDTO> getRegisteredUsers(@PathVariable Long id) {
        return eventService.getRegisteredUsers(id);
    }

    /**
     * Retrieves the list of forum threads associated with a specific event.
     *
     * @param id The ID of the event.
     * @return A set of forum threads associated with the event.
     */
    @GetMapping("/{id}/forum")
    public Set<ForumThreadResponseDTO> getForumThreads(@PathVariable Long id) {
        return eventService.getForumThreads(id);
    }

    /**
     * Retrieves the location information of a specific event.
     *
     * @param id The ID of the event.
     * @return The location details of the event.
     */
    @GetMapping("/{id}/location")
    public LocationDTO getLocation(@PathVariable Long id) {
        return eventService.getLocation(id);
    }

    /**
     * Retrieves the organizer of a specific event.
     *
     * @param id The ID of the event.
     * @return The organizer of the event.
     */
    @GetMapping("/{id}/organizer")
    public UserResponseDTO getOrganizerById(@PathVariable Long id) {
        return eventService.getOrganizerByEventId(id);
    }

    /**
     * Retrieves the exchange day associated with a specific event.
     *
     * @param id The ID of the event.
     * @return The exchange day details associated with the event.
     */
    @GetMapping("/{id}/exchange-day")
    public ExchangeDayResponseDTO getExchangeDayById(@PathVariable Long id) {
        return eventService.getExchangeDayByEventId(id);
    }

    /**
     * Retrieves the list of feedbacks associated with a specific event.
     *
     * @param eventId The ID of the event.
     * @return A list of feedback responses for the event.
     */
    @GetMapping("/{eventId}/feedback")
    public List<FeedbackResponseDTO> getFeedbacksForEvent(@PathVariable Long eventId) {
        return feedbackService.getFeedbacksFromEventId(eventId);
    }

    /**
     * Retrieves a summary of feedback for a specific event.
     *
     * @param id The ID of the event.
     * @return A summary of feedback for the event.
     */
    @GetMapping("/{id}/summary")
    public FeedbackSummaryDTO getFeedbackSummary(@PathVariable Long id) {
        return feedbackService.generateFeedbackSummary(id);
    }

    /**
     * Retrieves a word cloud image for feedback data associated with a specific event.
     *
     * @param eventId The ID of the event.
     * @return The word cloud image for the event.
     * @throws IOException If an error occurs while generating the word cloud.
     */
    @GetMapping("/{eventId}/word-cloud")
    public ResponseEntity<Resource> getWordCloud(@PathVariable Long eventId) throws IOException {
        Resource file = feedbackService.getWordCloud(eventId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    /**
     * Retrieves the attendance token for a specific event.
     *
     * @param eventId The ID of the event.
     * @return A map containing the attendance token for the event.
     */
    @GetMapping("/{eventId}/attendance-token")
    public ResponseEntity<Map<String, String>> getAttendanceToken(@PathVariable Long eventId){
        return ResponseEntity.ok(eventService.getAttendanceToken(eventId));
    }

    /**
     * Retrieves the QR code for a specific event.
     *
     * @param eventId The ID of the event.
     * @return The QR code image for the event.
     * @throws IOException If an error occurs while generating the QR code.
     */
    @GetMapping("/{eventId}/qr-code")
    public ResponseEntity<Resource> getQrCode(@PathVariable Long eventId) throws IOException {
        Resource file = eventService.getQRCode(eventId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    @GetMapping("/{eventId}/resources")
    public ResponseEntity<List<EventResourceResponse>> getResourcesByEventId(@PathVariable Long eventId) {
        List<EventResourceResponse> resources = eventService.getResourcesByEventId(eventId);
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves the tags associated with a specific event.
     *
     * @param eventId The ID of the event.
     * @return A set of tags for the event.
     */
    @GetMapping("/{eventId}/tags")
    public ResponseEntity<Set<TagResponseDTO>> getTagsFromEvent(@PathVariable Long eventId) {
        Set<TagResponseDTO> tags = eventService.getTagsFromEvent(eventId);

        return ResponseEntity.ok(tags);
    }

    /**
     * Retrieves all experience levels available for events.
     *
     * @return A list of all experience levels.
     */
    @GetMapping("/experience-levels")
    public ResponseEntity<List<String>> getAllExperienceLevels(){
        return ResponseEntity.ok(eventService.getAllExperienceLevels());
    }

    /**
     * Creates a new event based on the provided request body.
     *
     * @param requestBody The data for the new event.
     * @return The created event.
     * @throws Exception If an error occurs while creating the event.
     */
    @PostMapping("")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO requestBody) throws Exception {
        EventResponseDTO savedDTO = eventService.createEvent(requestBody);
        URI location = URI.create("/api/events/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
    }

    /**
     * Confirms attendance for a specific event.
     *
     * @param eventId The ID of the event.
     * @param requestBody The attendance confirmation details.
     */
    @PostMapping("/{eventId}/attendance")
    public void confirmAttendance(@PathVariable Long eventId, @RequestBody ConfirmAttendanceDTO requestBody) {
        eventService.confirmAttendance(eventId, requestBody);
    }

    /**
     * Updates an existing event based on the provided request body.
     *
     * @param id The ID of the event to update.
     * @param requestBody The data for updating the event.
     * @return The updated event.
     */
    @PutMapping("/{id}")
    public EventResponseDTO updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequestDTO requestBody) {
        return eventService.updateEvent(id, requestBody);
    }

    /**
     * Deletes a specific event by its ID.
     *
     * @param id The ID of the event to delete.
     * @return A response indicating that the event has been deleted.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
