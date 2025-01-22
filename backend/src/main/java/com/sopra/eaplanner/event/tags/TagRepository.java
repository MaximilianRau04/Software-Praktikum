package com.sopra.eaplanner.event.tags;

import com.sopra.eaplanner.event.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT t.name FROM Tag t WHERE t.name IN :names")
    List<String> findExistingTagNames(@Param("names") Set<String> names);

    @Query("SELECT t FROM Tag t WHERE t.name IN :names")
    Set<Tag> findExistingTags(@Param("names") Set<String> names);

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t.name = :tagName")
    List<Event> findByTagsName(@Param("tagName") String tagName);

    @Query("SELECT e FROM Event e JOIN e.tags t WHERE t.name IN :tagNames GROUP BY e HAVING COUNT(t) = :tagCount")
    List<Event> findByTagsNames(@Param("tagNames") List<String> tagNames, @Param("tagCount") Long tagCount);
}
