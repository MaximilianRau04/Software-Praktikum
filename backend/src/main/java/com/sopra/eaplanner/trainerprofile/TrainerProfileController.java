package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainerProfiles")
public class TrainerProfileController {
    // TODO: Work in progress
    @Autowired
    private TrainerProfileService trainerProfileService;

    @GetMapping("")
    public Iterable<Event> getAllTrainerProfiles() {
        return null;
    }

    @GetMapping("/{id}")
    public EventDTO getTrainerProfilePostById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("")
    public Event createTrainerProfile(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public void deleteTrainerProfile(@PathVariable Long id) {
        return;
    }
}
