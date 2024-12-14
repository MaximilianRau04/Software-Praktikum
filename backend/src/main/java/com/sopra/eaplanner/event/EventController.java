package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
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

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    FeedbackService feedbackService;

    @GetMapping("")
    public Iterable<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/registeredUsers")
    public Iterable<UserResponseDTO> getRegisteredUsers(@PathVariable Long id) {
        return eventService.getRegisteredUsers(id);
    }

    @GetMapping("/{id}/organizer")
    public UserResponseDTO getOrganizerById(@PathVariable Long id) {
        return eventService.getOrganizerByEventId(id);
    }

    @GetMapping("/{id}/exchange-day")
    public ExchangeDayResponseDTO getExchangeDayById(@PathVariable Long id) {
        return eventService.getExchangeDayByEventId(id);
    }

    @GetMapping("/{eventId}/feedback")
    public List<FeedbackResponseDTO> getFeedbacksForEvent(@PathVariable Long eventId) {
        return feedbackService.getFeedbacksFromEventId(eventId);
    }

    @GetMapping("/{id}/summary")
    public FeedbackSummaryDTO getFeedbackSummary(@PathVariable Long id) {
        return feedbackService.generateFeedbackSummary(id);
    }

    @GetMapping("/{eventId}/qr-code")
    public ResponseEntity<Resource> getQrCode(@PathVariable Long eventId) throws IOException {
        Resource file = eventService.getQRCode(eventId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    @PostMapping("")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO requestBody) throws Exception {
        EventResponseDTO savedDTO = eventService.createEvent(requestBody);
        URI location = URI.create("/api/events/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
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
