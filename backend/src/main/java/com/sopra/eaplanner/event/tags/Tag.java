package com.sopra.eaplanner.event.tags;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tag must contain a name")
    @Size(min = 3, max = 25, message = "Tag name must be between 3 and 25 characters")
    @Pattern(regexp = "^[a-z A-Z]*$", message = "Tag name must be alphabetic")
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Event> events = new HashSet<>();

    @ManyToMany(mappedBy = "expertiseTags")
    private Set<TrainerProfile> trainerProfiles = new HashSet<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<TrainerProfile> getTrainerProfiles() {
        return trainerProfiles;
    }

    public void setTrainerProfiles(Set<TrainerProfile> trainerProfiles) {
        this.trainerProfiles = trainerProfiles;
    }

}
