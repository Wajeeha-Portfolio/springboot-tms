package com.test.tms.services;

import com.test.tms.entities.Translation;
import com.test.tms.repositories.TranslationRepo;
import com.test.tms.requests.TranslationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j

public class TranslationService {
    @Autowired
    TranslationRepo translationRepo;

    public void addTranslation(TranslationRequest request) {
        // Implementation to add translation
        if (StringUtils.isEmpty(request.getKey()) || StringUtils.isEmpty(request.getContent()) || StringUtils.isEmpty(request.getLocale())
                || request.getTags().isEmpty()) {
            log.error("Key, content, locale, and tags must not be empty");
            throw new IllegalArgumentException("Invalid input");
        }

        Translation translation = Translation.builder()
                .key(request.getKey())
                .locale(request.getLocale())
                .content(request.getContent())
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .build();
        translationRepo.save(translation);
    }

    public void loadData() {

    }

    public void updateTranslation(Long id, TranslationRequest request) {
        if (StringUtils.isEmpty(request.getKey()) || StringUtils.isEmpty(request.getContent())
                || StringUtils.isEmpty(request.getLocale()) || request.getTags().isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<Translation> opt = translationRepo.findById(id);
        if (opt.isPresent()) {
            Translation t = opt.get();
            t.setKey(request.getKey());
            t.setContent(request.getContent());
            t.setLocale(request.getLocale());
            t.setTags(request.getTags());
            translationRepo.save(t);
        } else {
            throw new IllegalArgumentException("Translation not found");
        }
    }

    public Translation getTranslationById(Long id) {
        Optional<Translation> opt = translationRepo.findById(id);
        return opt.orElse(null);
    }

    public List<Translation> searchTranslations(String key, String content, String tag) {
        return translationRepo.searchAllByKeyOrContent(key, content);
    }
}
