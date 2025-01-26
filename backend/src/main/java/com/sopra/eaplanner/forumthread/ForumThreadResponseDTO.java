package com.sopra.eaplanner.forumthread;

import com.sopra.eaplanner.forumpost.dtos.ForumPostResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ForumThreadResponseDTO {

    private Long threadId;

    private String title;

    private String description;

    private List<ForumPostResponseDTO> forumPosts;

    public ForumThreadResponseDTO(Long threadId, String title, String description, List<ForumPostResponseDTO> forumPosts) {
        this.threadId = threadId;
        this.title = title;
        this.description = description;
        this.forumPosts = forumPosts;
    }

    public ForumThreadResponseDTO(ForumThread forumThread) {
        this.threadId = forumThread.getId();
        this.title = forumThread.getTitle();
        this.description = forumThread.getDescription();
        this.forumPosts = forumThread.getForumPosts().stream().map(ForumPostResponseDTO::new).collect(Collectors.toList());
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ForumPostResponseDTO> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPostResponseDTO> forumPosts) {
        this.forumPosts = forumPosts;
    }
}
