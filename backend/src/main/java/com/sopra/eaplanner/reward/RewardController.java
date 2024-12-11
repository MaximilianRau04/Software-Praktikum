package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
   // TODO: Work in progress
    @Autowired
    private RewardService rewardService;

    @GetMapping("")
    public Iterable<Event> getAllForumPosts() {
        return null;
    }

    @GetMapping("/{id}")
    public EventDTO getForumPostById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("")
    public Event createForumPost(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        return;
    }
}
