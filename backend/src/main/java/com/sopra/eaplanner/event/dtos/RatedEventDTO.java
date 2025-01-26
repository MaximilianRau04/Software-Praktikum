package com.sopra.eaplanner.event.dtos;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.ExperienceLevel;
import com.sopra.eaplanner.event.tags.TagResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for representing detailed information about an event,
 * including its average rating and associated tags.
 *
 * <p>This class is used to provide an enriched view of an event, combining event details
 * with additional metadata such as the average rating and a set of tags. It is designed
 * for scenarios where events are displayed with ratings and categorized information.</p>
 *
 * <p>The class includes a constructor to initialize the DTO using an {@link Event} entity,
 * along with the average rating and tags, ensuring a seamless conversion from entity to DTO.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   RatedEventDTO ratedEvent = new RatedEventDTO(event, 4.5, tags);
 *   System.out.println(ratedEvent.getAverageRating()); // Outputs: 4.5
 * </pre>
 */
public class RatedEventDTO {
    private Long id;
    private Long exchangeDayId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String name;
    private String description;
    private ExperienceLevel recommendedExperience;
    private Double averageRating;
    private Set<TagResponseDTO> tags;

    public RatedEventDTO(Event event, Double averageRating, Set<TagResponseDTO> tags) {
        this.id = event.getId();
        this.exchangeDayId = event.getExchangeDay().getId();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.name = event.getName();
        this.description = event.getDescription();
        this.recommendedExperience = event.getRecommendedExperience();
        this.averageRating = averageRating;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public Long getExchangeDayId() {
        return exchangeDayId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ExperienceLevel getRecommendedExperience() {
        return recommendedExperience;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Set<TagResponseDTO> getTags() {
        return tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExchangeDayId(Long exchangeDayId) {
        this.exchangeDayId = exchangeDayId;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecommendedExperience(ExperienceLevel recommendedExperience) {
        this.recommendedExperience = recommendedExperience;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void setTags(Set<TagResponseDTO> tags) {
        this.tags = tags;
    }
}

