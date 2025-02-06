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

    @Query(value = """
            SELECT position AS rank, user_id AS userId, username, total_points AS totalPoints
            FROM (
                SELECT
                    id AS user_id,
                    username,
                    total_points,
                    RANK() OVER (
                        ORDER BY total_points DESC, username ASC
                    ) AS position
                FROM users
            ) AS ranked_users
            WHERE (:search IS NULL OR LOWER(username) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY rank ASC
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM users
                    WHERE (:search IS NULL OR LOWER(username) LIKE LOWER(CONCAT('%', :search, '%')))
                    """,
            nativeQuery = true)
    Page<LeaderboardEntry> findLeaderboardEntries(
            @Param("search") String search,
            Pageable pageable
    );


    @Query(value = """
            SELECT position AS rank, user_id AS userId, username, total_points AS totalPoints
            FROM (
                SELECT
                    id AS user_id,
                    username,
                    total_points,
                    ROW_NUMBER() OVER (
                        ORDER BY
                            CASE
                                WHEN total_points > 0 THEN total_points
                                ELSE NULL
                            END DESC,
                            CASE
                                WHEN total_points = 0 THEN username
                                ELSE NULL
                            END ASC
                    ) AS position
                FROM users
            ) AS ranked_users
            WHERE user_id = :userId
            """, nativeQuery = true)
    LeaderboardEntry findCurrentUserPosition(@Param("userId") Long userId);

}
