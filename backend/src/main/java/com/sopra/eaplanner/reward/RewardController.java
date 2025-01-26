package com.sopra.eaplanner.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/badge")
    public ResponseEntity<Resource> getRewardBadge(@RequestParam("type") Reward.Type type, @RequestParam("currentLevel") Integer currentLevel) {
        Resource file = rewardService.getBadgePNG(type, currentLevel);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }
}
