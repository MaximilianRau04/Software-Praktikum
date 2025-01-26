package com.sopra.eaplanner.event.tags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for handling tag-related operations such as retrieving, creating, and deleting tags.
 *
 * <p>This controller exposes endpoints for clients to interact with the tag data, allowing users to perform
 * basic CRUD operations on tags in the system. The controller delegates the business logic to the {@link TagService}.</p>
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * Retrieves all tags in the system.
     *
     * <p>This endpoint returns a list of all existing tags in the system. The response contains a collection
     * of {@link TagResponseDTO} objects representing the tags.</p>
     *
     * @return a list of {@link TagResponseDTO} objects representing the tags
     */
    @GetMapping
    public List<TagResponseDTO> getAllTags() {
        return tagService.getTags();
    }

    /**
     * Creates a new tag.
     *
     * <p>This endpoint allows the creation of a new tag. The tag data is provided in the request body, and upon
     * successful creation, the response contains the created tag as a {@link TagResponseDTO} object.</p>
     *
     * @param tag the {@link Tag} object to be created
     * @return the created {@link TagResponseDTO}
     */
    @PostMapping
    public TagResponseDTO createTag(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }

    /**
     * Deletes a tag by its ID.
     *
     * <p>This endpoint allows the deletion of a tag by its unique ID. If the tag is successfully deleted, the response
     * returns a status of {@link ResponseEntity#noContent()}.</p>
     *
     * @param tagId the ID of the tag to be deleted
     * @return a {@link ResponseEntity} with a no content status
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
