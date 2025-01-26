package com.sopra.eaplanner.forumpost;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.forumthread.ForumThread;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Represents a forum post within a specific forum thread. A forum post is associated with a particular user (author)
 * and can either be anonymous or not. The post includes content, a creation timestamp, and a reference to the thread
 * in which it is posted.
 *
 * <p>Forum posts are stored in the database with a unique identifier, the content of the post, a timestamp indicating
 * when the post was created, and the relationship to the forum thread and the author of the post.</p>
 *
 * <p>This entity is used to manage posts within a forum system, allowing users to participate in discussions by
 * creating posts that belong to specific threads.</p>
 */
@Entity
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "forum_thread_id")
    @NotNull
    @JsonBackReference
    private ForumThread forumThread;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private Boolean isAnonymous;

    public ForumPost() {
    }

    public ForumPost(String content, ForumThread forumThread, User author, Boolean isAnonymous) {
        this.content = content;
        this.forumThread = forumThread;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.isAnonymous = isAnonymous;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ForumThread getForumThread() {
        return forumThread;
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}
