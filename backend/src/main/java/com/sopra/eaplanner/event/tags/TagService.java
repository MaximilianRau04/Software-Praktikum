package com.sopra.eaplanner.event.tags;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<TagResponseDTO> getTags() {
        Iterable<Tag> tags = tagRepository.findAll();
        List<TagResponseDTO> dtos = new ArrayList<>();
        for (Tag tag : tags) {
            dtos.add(new TagResponseDTO(tag));
        }
        return dtos;
    }

    public TagResponseDTO createTag(Tag tag) {
        Iterable<Tag> presentTags = tagRepository.findAll();

        for (Tag presentTag : presentTags) {
            if (presentTag.getName().equals(tag.getName())) {
                throw new EntityExistsException("This tag already exists");
            }
        }

        return new TagResponseDTO(tagRepository.save(tag));
    }

    public void deleteTag(Long tagId) {
        Tag tagToDelete = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("This tag does not exist"));

        tagToDelete.getEvents().forEach(event -> event.getTags().remove(tagToDelete));

        tagRepository.delete(tagToDelete);
    }

    public Set<Tag> mergeAndGetTagsFromRequest(Set<String> tagNames){
        List<String> existingTagNames = tagRepository.findExistingTagNames(tagNames);

        Set<String> missingTagNames = tagNames.stream()
                .map(String::trim)
                .filter(name -> !existingTagNames.contains(name.trim()))
                .collect(Collectors.toSet());

        missingTagNames.forEach(System.out::println);

        Set<Tag> newTags = missingTagNames.stream()
                .map(Tag::new)
                .collect(Collectors.toSet());

        newTags.stream().map(Tag::getName).forEach(System.out::println);

        tagRepository.saveAll(newTags);

        return tagRepository.findExistingTags(tagNames);
    }
}
