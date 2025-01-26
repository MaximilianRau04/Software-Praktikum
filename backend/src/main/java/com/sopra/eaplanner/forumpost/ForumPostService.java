package com.sopra.eaplanner.forumpost;

import com.sopra.eaplanner.forumpost.dtos.ForumPostDTO;
import com.sopra.eaplanner.forumpost.dtos.ForumPostResponseDTO;
import com.sopra.eaplanner.forumthread.ForumThread;
import com.sopra.eaplanner.forumthread.ForumThreadRepository;
import com.sopra.eaplanner.forumthread.notification.ForumResponseService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling the business logic associated with {@link ForumPost} entities.
 * Provides methods to retrieve, create, update, and delete forum posts, as well as handling notifications
 * related to forum posts.
 * <p>
 * The service interacts with the {@link ForumPostRepository}, {@link ForumThreadRepository},
 * {@link UserRepository}, and {@link ForumResponseService} to perform the necessary operations.
 * </p>
 */
@Service
public class ForumPostService {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ForumThreadRepository forumThreadRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumResponseService forumResponseService;

    /**
     * Retrieves all forum posts.
     *
     * @return an iterable collection of {@link ForumPostResponseDTO} representing all forum posts.
     */
    public Iterable<ForumPostResponseDTO> getForumPosts() {
        return forumPostRepository.findAll().stream().map(ForumPostResponseDTO::new).collect(Collectors.toSet());
    }

    /**
     * Retrieves a specific forum post by its ID.
     *
     * @param id the ID of the forum post to retrieve.
     * @return the {@link ForumPostResponseDTO} representing the forum post.
     * @throws EntityNotFoundException if the forum post with the specified ID is not found.
     */
    public ForumPostResponseDTO getForumPost(Long id) {
        Optional<ForumPost> forumPost = forumPostRepository.findById(id);
        if (forumPost.isEmpty()) {
            throw new EntityNotFoundException("Forum Post with the id " + id + " not found");
        }
        return new ForumPostResponseDTO(forumPost.get());
    }

    /**
     * Creates a new forum post.
     *
     * @param forumPostDTO the {@link ForumPostDTO} containing the data for the new forum post.
     * @return the {@link ForumPostResponseDTO} representing the created forum post.
     * @throws EntityNotFoundException if the associated forum thread or user cannot be found.
     */
    public ForumPostResponseDTO createForumPost(ForumPostDTO forumPostDTO) {
        ForumThread forumThread = forumThreadRepository.findById(forumPostDTO.getForumThreadId())
                .orElseThrow(() -> new EntityNotFoundException("ForumThread with id " + forumPostDTO.getForumThreadId() + " not found"));

        User author = userRepository.findById(forumPostDTO.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + forumPostDTO.getAuthorId() + " not found"));

        ForumPost forumPost = new ForumPost(forumPostDTO.getContent(), forumThread, author, forumPostDTO.isAnonymous());

        if (forumPost.getCreatedAt() == null) {
            forumPost.setCreatedAt(LocalDateTime.now());
        }

        forumResponseService.sendResponseNotification(forumThread.getEvent().getId(),forumThread.getId(),author, forumPost.isAnonymous());

        return new ForumPostResponseDTO(forumPostRepository.save(forumPost));
    }

    /**
     * Updates an existing forum post.
     *
     * @param id the ID of the forum post to update.
     * @param forumPostDTO the {@link ForumPostDTO} containing the updated data for the forum post.
     * @return the {@link ForumPostResponseDTO} representing the updated forum post.
     * @throws EntityNotFoundException if the forum post with the specified ID or the associated forum thread or user cannot be found.
     */
    public ForumPostResponseDTO updateForumPost(Long id, ForumPostDTO forumPostDTO) {
        Optional<ForumPost> forumPostOptional = forumPostRepository.findById(id);
        if (forumPostOptional.isEmpty()) {
            throw new EntityNotFoundException("Forum Post with id " + id + " not found");
        }

        ForumThread forumThread = forumThreadRepository.findById(forumPostDTO.getForumThreadId())
                .orElseThrow(() -> new EntityNotFoundException("ForumThread with id " + forumPostDTO.getForumThreadId() + " not found"));

        User author = userRepository.findById(forumPostDTO.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + forumPostDTO.getAuthorId() + " not found"));

        ForumPost forumPost = forumPostOptional.get();
        forumPost.setContent(forumPostDTO.getContent());
        forumPost.setForumThread(forumThread);
        forumPost.setAuthor(author);

        if (forumPost.getCreatedAt() == null) {
            forumPost.setCreatedAt(forumPostOptional.get().getCreatedAt());
        }

        forumResponseService.sendResponseNotification(forumThread.getEvent().getId(),forumThread.getId(),author, forumPost.isAnonymous());

        return new ForumPostResponseDTO(forumPostRepository.save(forumPost));
    }

    /**
     * Deletes a forum post by its ID.
     *
     * @param id the ID of the forum post to delete.
     */
    public void deleteForumPost(Long id) {
        forumPostRepository.deleteById(id);
    }
}
