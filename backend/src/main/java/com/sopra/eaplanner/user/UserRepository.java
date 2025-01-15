package com.sopra.eaplanner.user;

import com.sopra.eaplanner.reward.leaderboard.LeaderboardEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u.id AS userId, u.username AS username, SUM(r.points) AS totalPoints " +
            "FROM User u JOIN u.rewards r " +
            "GROUP BY u.id, u.username " +
            "ORDER BY totalPoints DESC")
    Page<LeaderboardEntry> findLeaderboardEntries(Pageable pageable);
}
