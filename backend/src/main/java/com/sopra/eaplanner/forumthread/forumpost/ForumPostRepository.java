package com.sopra.eaplanner.forumthread.forumpost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByForumThreadId(Long forumThreadId);
}
