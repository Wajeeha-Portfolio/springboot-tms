package com.test.tms.repositories;

import com.test.tms.entities.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TranslationRepo extends JpaRepository<Translation, Long> {
    List<Translation> findByKeyContainingIgnoreCase(String key);
    List<Translation> findByContentContainingIgnoreCase(String content);

    @Query("SELECT DISTINCT t FROM Translation t JOIN t.tags tag WHERE tag IN :tagNames")
    List<Translation> findByTagNamesOr(@Param("tagNames") Set<String> tagNames);

    @Query("SELECT DISTINCT t FROM Translation t LEFT JOIN t.tags tag WHERE " +
            "(LOWER(t.key) LIKE LOWER(CONCAT('%', :key, '%')) OR " +
            "LOWER(t.content) LIKE LOWER(CONCAT('%', :content, '%'))) " +
            "OR (:tagNames IS NULL OR tag IN :tagNames)")
    List<Translation> searchByKeyOrContentWithTags(@Param("key") String key,
                                                    @Param("content") String content,
                                                   @Param("tagNames") Set<String> tagNames);
    List<Translation> findByLocale(String locale);
    List<Translation> findAll();

}
