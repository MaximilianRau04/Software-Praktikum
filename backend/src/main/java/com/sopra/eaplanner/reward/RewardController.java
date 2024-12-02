package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/rewards")
    public Iterable<Event> getAllForumPosts() {
        return null;
    }

    @GetMapping("/rewards/{id}")
    public EventDTO getForumPostById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/rewards")
    public Event createForumPost(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/rewards/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        return;
    }
}
