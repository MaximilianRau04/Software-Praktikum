package com.sopra.eaplanner.forumthread;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.forumpost.ForumPost;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class ForumThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private Event event;

    @OneToMany(mappedBy = "forumThread", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ForumPost> forumPosts;

    public ForumThread() {
    }

    public ForumThread(String title, String description, Event event) {
        this.title = title;
        this.description = description;
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }
}
