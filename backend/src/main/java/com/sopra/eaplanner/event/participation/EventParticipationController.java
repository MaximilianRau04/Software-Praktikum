package com.sopra.eaplanner.event.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participations")
public class EventParticipationController {

    @Autowired
    private EventParticipationService eventParticipationService;

    @GetMapping("/{userId}/{eventId}")
    public ResponseEntity<EventParticipationDTO> getParticipation(@PathVariable("userId") Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventParticipationService.getParticipation(userId, eventId));
    }

    @GetMapping("/userId}")
    public ResponseEntity<EventPart>
}
