package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    FeedbackService feedbackService;

    @GetMapping("")
    public Iterable<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.getEventWithUserIds(id);
    }

    @GetMapping("/{eventId}/feedbacks")
    public List<FeedbackResponseDTO> getFeedbacksForEvent(@PathVariable Long eventId) {
        return feedbackService.getFeedbacksFromEventId(eventId);
    }

    @GetMapping("/{eventId}/qr-code")
    public ResponseEntity<Resource> getQrCode(@PathVariable Long eventId) throws IOException {
        Resource file = eventService.getQRCode(eventId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    @PostMapping("")
    public Event createEvent(@Valid @RequestBody EventDTO requestBody) throws Exception {
        return eventService.createEvent(requestBody);
    }

    @PostMapping("/{eventId}/attendance")
    public void confirmAttendance(@PathVariable Long eventId, @RequestBody ConfirmAttendanceDTO requestBody) {
        eventService.confirmAttendance(eventId, requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
