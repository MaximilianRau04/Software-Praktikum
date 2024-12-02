package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Type {
        ATTENDER, FEEDBACK_GIVER
        // find more reward types
    }

    private Type type;

    private Integer points;

    private String badge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public Reward() {
    }

    public Reward(Type type, Integer points, String badge, User user) {
        this.type = type;
        this.points = points;
        this.badge = badge;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
