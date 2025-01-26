package com.sopra.eaplanner.forumpost.dtos;

/**
 * Data Transfer Object (DTO) for creating and updating a forum post. This DTO is used to encapsulate the input data
 * for a forum post request (either creation or update).
 *
 * <p>It contains the necessary information about a forum post, including the post content, the associated forum thread,
 * the author of the post, and whether the post is anonymous.</p>
 */
public class ForumPostDTO {

    private String content;
    private Long forumThreadId;
    private Long authorId;

    private Boolean isAnonymous;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getForumThreadId() {
        return forumThreadId;
    }

    public void setForumThreadId(Long forumThreadId) {
        this.forumThreadId = forumThreadId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}
