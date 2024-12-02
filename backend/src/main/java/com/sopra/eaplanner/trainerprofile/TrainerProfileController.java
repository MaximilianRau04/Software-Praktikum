package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TrainerProfileController {

    @Autowired
    private TrainerProfileService trainerProfileService;

    @GetMapping("/trainerProfiles")
    public Iterable<Event> getAllTrainerProfiles() {
        return null;
    }

    @GetMapping("/trainerProfiles/{id}")
    public EventDTO getTrainerProfilePostById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/trainerProfiles")
    public Event createTrainerProfile(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/trainerProfiles/{id}")
    public void deleteTrainerProfile(@PathVariable Long id) {
        return;
    }
}
