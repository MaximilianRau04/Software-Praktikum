package com.sopra.eaplanner.reward.leaderboard;

import com.sopra.eaplanner.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public Page<LeaderboardEntry> generateLeaderboard(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findLeaderboardEntries(pageable);
    }
}
