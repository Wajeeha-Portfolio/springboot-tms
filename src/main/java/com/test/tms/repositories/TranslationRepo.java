package com.test.tms.repositories;

import com.test.tms.entities.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepo extends JpaRepository<Translation, Long> {
//    @Query("SELECT t FROM Translation t JOIN t.tags tag " +
//            "WHERE (:key IS NULL OR t.key = :key) " +
//            "AND (:locale IS NULL OR t.locale = :locale) " +
//            "AND (:tag IS NULL OR tag = :tag)")
//    List<Translation> search(@Param("key") String key,
//                             @Param("locale") String locale,
//                             @Param("tag") String tag);

    List<Translation> searchAllByKeyOrContent(String field, String content);
//    List<Translation> searchAllByFieldOrContentOrTags(String field, String content, String tag);
}
