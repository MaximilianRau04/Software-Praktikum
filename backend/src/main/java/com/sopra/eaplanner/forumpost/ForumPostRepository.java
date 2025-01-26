package com.sopra.eaplanner.forumpost;

import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByForumThreadId(Long forumThreadId);

    /**
     * Finds all forum posts that belong to a specific user.
     *
     * @param user the user whose forum posts are being searched for
     * @return a list of {@link ForumPost} entities that belong to the specified user
     */
    List<ForumPost> findByAuthor(User user);
}
