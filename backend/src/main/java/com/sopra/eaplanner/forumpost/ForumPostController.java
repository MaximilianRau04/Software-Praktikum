package com.sopra.eaplanner.forumpost;

import com.sopra.eaplanner.forumpost.dtos.ForumPostDTO;
import com.sopra.eaplanner.forumpost.dtos.ForumPostResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing forum posts in a forum system. This controller provides endpoints to perform CRUD (Create,
 * Read, Update, Delete) operations on forum posts.
 *
 * <p>Forum posts are part of a specific thread and belong to a user (author). The controller allows retrieving all forum
 * posts, retrieving a specific post by its ID, creating new posts, updating existing posts, and deleting posts.</p>
 *
 * <p>The controller is mapped to the `/api/forumposts` endpoint and relies on the {@link ForumPostService} service
 * layer for business logic.</p>
 */
@RestController
@RequestMapping("/api/forumposts")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    /**
     * Retrieves all forum posts in the system.
     *
     * @return an iterable collection of {@link ForumPostResponseDTO} objects representing all forum posts.
     */
    @GetMapping("")
    public Iterable<ForumPostResponseDTO> getAllForumPosts() {
        return forumPostService.getForumPosts();
    }

    /**
     * Retrieves a forum post by its unique ID.
     *
     * @param id the ID of the forum post to retrieve.
     * @return a {@link ForumPostResponseDTO} object representing the forum post with the given ID.
     */
    @GetMapping("/{id}")
    public ForumPostResponseDTO getForumPostById(@PathVariable Long id) {
        return forumPostService.getForumPost(id);
    }

    /**
     * Creates a new forum post based on the provided request data.
     *
     * @param requestBody the data for the new forum post, encapsulated in a {@link ForumPostDTO} object.
     * @return a {@link ForumPostResponseDTO} object representing the newly created forum post.
     */
    @PostMapping("")
    public ForumPostResponseDTO createForumPost(@Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.createForumPost(requestBody);
    }

    /**
     * Updates an existing forum post based on the provided ID and request data.
     *
     * @param id the ID of the forum post to update.
     * @param requestBody the updated data for the forum post, encapsulated in a {@link ForumPostDTO} object.
     * @return a {@link ForumPostResponseDTO} object representing the updated forum post.
     */
    @PutMapping("/{id}")
    public ForumPostResponseDTO updateForumPost(@PathVariable Long id, @Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.updateForumPost(id, requestBody);
    }

    /**
     * Deletes a forum post by its unique ID.
     *
     * @param id the ID of the forum post to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        forumPostService.deleteForumPost(id);
    }
}
