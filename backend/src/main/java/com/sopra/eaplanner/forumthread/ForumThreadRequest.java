package com.sopra.eaplanner.forumthread;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ForumThreadRequest {

    @NotBlank(message = "Title cannot be empty.")
    private String title;

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @NotNull(message = "Event cannot be null.")
    private Long eventId;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
