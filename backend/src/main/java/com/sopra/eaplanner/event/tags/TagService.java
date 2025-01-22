package com.sopra.eaplanner.event.tags;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
