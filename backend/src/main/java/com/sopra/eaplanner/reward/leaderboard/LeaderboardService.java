package com.sopra.eaplanner.reward.leaderboard;

import com.sopra.eaplanner.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public Page<LeaderboardEntry> generateLeaderboard(int page, int size, String search, String sort) {
        // sorted pageable
        Sort.Direction direction = Sort.Direction.fromString(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "totalPoints"));

        return userRepository.findLeaderboardEntries(
                search != null ? "%" + search + "%" : null,
                pageable
        );
    }
}
