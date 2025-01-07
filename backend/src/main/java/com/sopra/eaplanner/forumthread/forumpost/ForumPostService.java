package com.sopra.eaplanner.forumthread.forumpost;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForumPostService {

    @Autowired
    private ForumPostRepository forumPostRepository;

    public Iterable<ForumPost> getForumPosts() {
        return forumPostRepository.findAll();
    }

    public ForumPost getForumPost(Long id) {
        Optional<ForumPost> forumPost = forumPostRepository.findById(id);
        if(forumPost.isEmpty()) {
            throw new EntityNotFoundException("Forum Post with the id " + id + " not found");
        }
        return forumPost.get();
    }

    public ForumPost createForumPost(ForumPost forumPost) {
        return forumPostRepository.save(forumPost);
    }

    public ForumPost updateForumPost(Long id, ForumPost forumPost) {
        Optional<ForumPost> forumPostOptional = forumPostRepository.findById(id);
        if (forumPostOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }
        forumPost.setId(id);
        return forumPostRepository.save(forumPost);
    }

    public void deleteForumPost(Long id) {
        forumPostRepository.deleteById(id);
    }
}
