package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserTagWeightRepository extends JpaRepository<UserTagWeight, Long> {

    Optional<UserTagWeight> findByUserAndTag(User user, Tag tag);

    @Query("SELECT utw.tag FROM UserTagWeight utw WHERE utw.user = :user ORDER BY utw.tagWeight DESC")
    List<Tag> findTopTagsForUser(@Param("user") User user);

    @Query("SELECT utw FROM UserTagWeight utw WHERE utw.user IN :users AND utw.tag IN :tags")
    List<UserTagWeight> findByUsersAndTags(@Param("users") Set<User> users, @Param("tags") Set<Tag> tags);
}
