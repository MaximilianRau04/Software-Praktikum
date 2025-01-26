package com.sopra.eaplanner.event.tags;

/**
 * Data Transfer Object (DTO) representing a response for a {@link Tag} entity.
 *
 * <p>This class is used to transfer information about a tag, specifically its ID and name, between layers of the application.
 * It is typically used in API responses to provide a simplified representation of the {@link Tag} entity.</p>
 */
public class TagResponseDTO {

    private Long id;

    private String name;

    public TagResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagResponseDTO(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
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
}
