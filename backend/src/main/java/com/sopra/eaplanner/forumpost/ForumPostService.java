package com.sopra.eaplanner.forumpost;

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

    public Iterable<ForumPostResponseDTO> getForumPosts() {
        return forumPostRepository.findAll().stream().map(ForumPostResponseDTO::new).collect(Collectors.toSet());
    }

    public ForumPostResponseDTO getForumPost(Long id) {
        Optional<ForumPost> forumPost = forumPostRepository.findById(id);
        if (forumPost.isEmpty()) {
            throw new EntityNotFoundException("Forum Post with the id " + id + " not found");
        }
        return new ForumPostResponseDTO(forumPost.get());
    }

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

    public void deleteForumPost(Long id) {
        forumPostRepository.deleteById(id);
    }
}
