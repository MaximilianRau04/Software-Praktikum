package com.sopra.eaplanner.forumpost;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.forumthread.ForumThread;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ForumPostResponseDTO {
    private String content;

    private LocalDateTime createdAt;

    private Long threadId;

    private UserResponseDTO author;

    private Boolean isAnonymous;

    public ForumPostResponseDTO(String content, LocalDateTime createdAt, Long threadId, UserResponseDTO author, Boolean isAnonymous) {
        this.content = content;
        this.createdAt = createdAt;
        this.threadId = threadId;
        this.author = author;
        this.isAnonymous = isAnonymous;
    }

    public ForumPostResponseDTO(ForumPost forumPost) {
        this.content = forumPost.getContent();
        this.createdAt = forumPost.getCreatedAt();
        this.threadId = forumPost.getForumThread().getId();
        this.isAnonymous = forumPost.isAnonymous();
        this.author = new UserResponseDTO(forumPost.getAuthor());
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

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public UserResponseDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserResponseDTO author) {
        this.author = author;
    }
}
