package com.sopra.eaplanner.forumpost;

import com.sopra.eaplanner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link ForumPost} entities. This interface extends {@link JpaRepository}
 * to provide CRUD operations for {@link ForumPost} entities and a custom query method to retrieve posts by their
 * associated forum thread ID.
 *
 * <p>It offers a method to retrieve all forum posts that belong to a specific forum thread.</p>
 */
@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    /**
     * Finds all forum posts that belong to a specific forum thread.
     *
     * @param forumThreadId the ID of the forum thread to search for posts
     * @return a list of {@link ForumPost} entities that belong to the specified forum thread
     */
    List<ForumPost> findByForumThreadId(Long forumThreadId);

    /**
     * Finds all forum posts that belong to a specific user.
     *
     * @param user the user whose forum posts are being searched for
     * @return a list of {@link ForumPost} entities that belong to the specified user
     */
    List<ForumPost> findByAuthor(User user);
}
