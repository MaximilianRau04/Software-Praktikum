package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
   // TODO: Work in progress
    @Autowired
    private RewardService rewardService;

    @GetMapping("/badge")
    public ResponseEntity<Resource> getRewardBadge(@RequestParam("type") Reward.Type type, @RequestParam("threshold") Integer threshold) {
        Resource file = rewardService.getBadgePNG(type, threshold);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    @GetMapping("")
    public Iterable<Event> getAllForumPosts() {
        return null;
    }

    @GetMapping("/{id}")
    public EventResponseDTO getForumPostById(@PathVariable Long id) {
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
