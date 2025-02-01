package com.sopra.eaplanner.reward.leaderboard;

import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<LeaderboardEntry>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "desc") String sort) {
        Page<LeaderboardEntry> leaderboard = leaderboardService.generateLeaderboard(page, size, search, sort);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/current-position")
    public ResponseEntity<LeaderboardEntry> getCurrentUserPosition(
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LeaderboardEntry position = userRepository.findCurrentUserPosition(currentUser.getId());
        return ResponseEntity.ok(position);
    }
}
