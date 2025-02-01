package com.sopra.eaplanner.reward.leaderboard;

public interface LeaderboardEntry {
    Long getUserId();
    String getUsername();
    Integer getTotalPoints();
    Integer getRank();
}
