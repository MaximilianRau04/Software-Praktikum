package com.sopra.eaplanner.event.tags;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a tag, which is used to categorize or label events and trainer profiles.
 *
 * <p>A tag can be associated with multiple events and trainer profiles. Tags help in organizing events
 * and connecting trainers to their areas of expertise. Tags have constraints on their name to ensure they
 * are properly formatted and easily identifiable.</p>
 */
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tag must contain a name")
    @Size(max = 25, message = "Tag name must be less than 25 characters")
    @Pattern(regexp = "^[a-z A-Z]*$", message = "Tag name must be alphabetic")
    private String name;

    /**
     * The set of events associated with this tag.
     *
     * <p>Each tag can be related to multiple events, allowing the events to be grouped by tags for easier
     * categorization and searchability.</p>
     */
    @ManyToMany(mappedBy = "tags")
    private Set<Event> events = new HashSet<>();

    /**
     * The set of trainer profiles associated with this tag.
     *
     * <p>Each tag can be associated with multiple trainer profiles, helping to identify the areas of expertise
     * of trainers and connect them with relevant events.</p>
     */
    @ManyToMany(mappedBy = "expertiseTags")
    private Set<TrainerProfile> trainerProfiles = new HashSet<>();

    @ManyToMany(mappedBy = "interestTags")
    private Set<User> users = new HashSet<>();

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
