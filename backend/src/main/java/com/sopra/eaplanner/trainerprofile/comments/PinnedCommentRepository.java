package com.sopra.eaplanner.trainerprofile.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinnedCommentRepository extends JpaRepository<PinnedComment, Long> {

    @Query("SELECT COUNT(pc) FROM PinnedComment pc WHERE pc.trainer.id = :trainerId")
    long countByTrainerId(@Param("trainerId") Long trainerId);

    @Query("SELECT pc FROM PinnedComment pc WHERE pc.trainer.id = :trainerId ORDER BY pc.pinnedAt DESC")
    List<PinnedComment> findByTrainerId(@Param("trainerId") Long trainerId);
}


