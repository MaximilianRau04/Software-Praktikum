package com.sopra.eaplanner.event.tags;

import com.sopra.eaplanner.event.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for performing CRUD operations on the {@link Tag} entity.
 *
 * <p>This repository provides additional methods to query tags and related entities (such as events) based on tag names.
 * Custom queries are defined using {@link Query} annotations to handle more specific use cases like finding existing tags
 * by name or retrieving events associated with certain tags.</p>
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    /**
     * Finds existing tag names from the provided set of names.
     *
     * <p>This method performs a query to check which tag names from the provided set already exist in the database.
     * It returns a list of existing tag names.</p>
     *
     * @param names the set of tag names to check for existence
     * @return a list of existing tag names
     */
    @Query("SELECT t.name FROM Tag t WHERE t.name IN :names")
    List<String> findExistingTagNames(@Param("names") Set<String> names);

    /**
     * Finds existing tags from the provided set of names.
     *
     * <p>This method performs a query to retrieve the {@link Tag} entities corresponding to the provided tag names.
     * It returns a set of {@link Tag} entities.</p>
     *
     * @param names the set of tag names to look up
     * @return a set of {@link Tag} entities that match the provided names
     */
    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    Set<Tag> findExistingTags(@Param("names") Set<String> names);

    /**
     * Finds events associated with all the provided tag names, ensuring each event has exactly the given number of tags.
     *
     * <p>This method performs a query to retrieve events that are associated with all the specified tag names and groups
     * the results by events. The query also filters events to only return those that have the exact number of tags matching
     * the provided count.</p>
     *
     * @param tagNames the list of tag names to search for
     * @param tagCount the expected number of tags associated with each event
     * @return a list of {@link Event} entities that have the specified number of tags
     */
    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t.name IN :tagNames GROUP BY e HAVING COUNT(t) = :tagCount")
    List<Event> findByTagsNames(@Param("tagNames") List<String> tagNames, @Param("tagCount") Long tagCount);
}
