package com.sopra.eaplanner.forumthread;

import com.sopra.eaplanner.forumpost.ForumPostResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ForumThreadResponseDTO {

    private String title;

    private String description;

    private List<ForumPostResponseDTO> forumPosts;

    public ForumThreadResponseDTO(String title, String description, List<ForumPostResponseDTO> forumPosts) {
        this.title = title;
        this.description = description;
        this.forumPosts = forumPosts;
    }

    public ForumThreadResponseDTO(ForumThread forumThread) {
        this.title = forumThread.getTitle();
        this.description = forumThread.getDescription();
        this.forumPosts = forumThread.getForumPosts().stream().map(ForumPostResponseDTO::new).collect(Collectors.toList());
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
