package com.sopra.eaplanner.event.tags;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing {@link Tag} entities.
 *
 * <p>This service provides business logic for creating, deleting, retrieving, and merging tags, ensuring that tags are handled consistently and correctly in the application.</p>
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * Retrieves all tags in the system and returns them as a list of {@link TagResponseDTO} objects.
     *
     * <p>This method fetches all tags from the database and maps them into a list of DTOs for use in API responses.</p>
     *
     * @return a list of {@link TagResponseDTO} objects representing all tags in the system
     */
    public List<TagResponseDTO> getTags() {
        Iterable<Tag> tags = tagRepository.findAll();
        List<TagResponseDTO> dtos = new ArrayList<>();
        for (Tag tag : tags) {
            dtos.add(new TagResponseDTO(tag, false));
        }
        return dtos;
    }

    /**
     * Creates a new {@link Tag} in the system, ensuring that the tag's name is unique.
     *
     * <p>This method checks whether the tag already exists in the database. If it does, an exception is thrown; otherwise, the tag is saved.</p>
     *
     * @param tag the {@link Tag} entity to be created
     * @return a {@link TagResponseDTO} representing the created tag
     * @throws EntityExistsException if a tag with the same name already exists
     */
    public TagResponseDTO createTag(Tag tag) {
        Iterable<Tag> presentTags = tagRepository.findAll();

        for (Tag presentTag : presentTags) {
            if (presentTag.getName().equals(tag.getName())) {
                throw new EntityExistsException("This tag already exists");
            }
        }

        return new TagResponseDTO(tagRepository.save(tag));
    }

    /**
     * Deletes an existing {@link Tag} from the system, removing it from all associated events.
     *
     * <p>This method ensures that the tag is disassociated from any events before it is deleted from the database.</p>
     *
     * @param tagId the ID of the tag to delete
     * @throws EntityNotFoundException if the tag does not exist
     */
    public void deleteTag(Long tagId) {
        Tag tagToDelete = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("This tag does not exist"));

        tagToDelete.getEvents().forEach(event -> event.getTags().remove(tagToDelete));

        tagRepository.delete(tagToDelete);
    }

    /**
     * Merges tags from a set of tag names, creating any new tags that do not exist in the system.
     *
     * <p>This method checks for existing tags and creates new tags for any missing names. The newly created tags are then saved in the database.</p>
     *
     * @param tagNames a set of tag names to be merged and retrieved
     * @return a set of {@link Tag} objects representing the merged tags
     */
    public Set<Tag> mergeAndGetTagsFromRequest(Set<String> tagNames){
        List<String> existingTagNames = tagRepository.findExistingTagNames(tagNames);

        Set<String> missingTagNames = tagNames.stream()
                .map(String::trim)
                .filter(name -> !existingTagNames.contains(name.trim()))
                .collect(Collectors.toSet());

        Set<Tag> newTags = missingTagNames.stream()
                .map(Tag::new)
                .collect(Collectors.toSet());

        newTags.stream().map(Tag::getName).forEach(System.out::println);

        tagRepository.saveAll(newTags);

        return tagRepository.findExistingTags(tagNames);
    }
}
