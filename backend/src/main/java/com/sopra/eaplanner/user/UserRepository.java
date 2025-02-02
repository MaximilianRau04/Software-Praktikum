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

    Optional<User> findByUsername(String username);

    @Query("SELECT u.id AS userId, u.username AS username, u.totalPoints AS totalPoints " +
            "FROM User u " +
            "WHERE (:search IS NULL OR LOWER(u.username) LIKE LOWER(concat('%', :search, '%')))")
    Page<LeaderboardEntry> findLeaderboardEntries(
            @Param("search") String search,
            Pageable pageable
    );

    @Query(value = """
            SELECT position as rank, user_id as userId, username, total_points as totalPoints 
            FROM (
                SELECT 
                    id AS user_id,
                    username,
                    total_points,
                    ROW_NUMBER() OVER (ORDER BY total_points DESC) AS position
                FROM users
            ) AS ranked_users 
            WHERE user_id = :userId
            """, nativeQuery = true)
    LeaderboardEntry findCurrentUserPosition(@Param("userId") Long userId);

}
