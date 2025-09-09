package com.test.tms.services;

import com.test.tms.Responses.ExportResponse;
import com.test.tms.entities.Translation;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.TranslationRepo;
import com.test.tms.requests.SearchRequest;
import com.test.tms.requests.TranslationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j

public class TranslationService {
    @Autowired
    TranslationRepo translationRepo;

    public Long addTranslation(TranslationRequest request) {
        // Validate input
        if (StringUtils.isEmpty(request.getKey()) || StringUtils.isEmpty(request.getContent())
                || StringUtils.isEmpty(request.getLocale()) || request.getTags().isEmpty()) {
            log.error("Key, content, locale, and tags must not be empty");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        Translation translation = Translation.builder()
                .key(request.getKey())
                .locale(request.getLocale())
                .content(request.getContent())
                .tags(request.getTags() != null ? request.getTags() : new HashSet<>())
                .build();
        translationRepo.save(translation);
        log.info("Translation added successfully with ID: {}", translation.getId());
        return translation.getId();
    }

    public void updateTranslation(Long id, TranslationRequest request) {
        // Validate input
        if (StringUtils.isEmpty(request.getKey()) || StringUtils.isEmpty(request.getContent())
                || StringUtils.isEmpty(request.getLocale()) || request.getTags().isEmpty()) {
            log.error("Key, content, locale, and tags must not be empty");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Invalid input");
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
            log.error("Translation not found with ID: {}", id);
            throw new CommonServiceException(HttpStatus.NOT_FOUND, "Translation not found");
        }
    }

    public Translation getTranslationById(Long id) {
        Optional<Translation> opt = translationRepo.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            log.error("Translation not found with ID: {}", id);
            throw new CommonServiceException(HttpStatus.NOT_FOUND, "Translation not found");
        }
    }

    public List<Translation> searchTranslations(SearchRequest request) {
        if (StringUtils.isEmpty(request.getKey()) && StringUtils.isEmpty(request.getContent()) && request.getTags().isEmpty()) {
            log.error("At least one search parameter (key, content, or tags) must be provided");
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "At least one search parameter (key, content, or tags) must be provided");
        }

        if (!StringUtils.isEmpty(request.getKey()) && !StringUtils.isEmpty(request.getContent())
                && !request.getTags().isEmpty()) {
            return translationRepo.searchByKeyOrContentWithTags(request.getKey(), request.getContent(), request.getTags());
        }

        if (!StringUtils.isEmpty(request.getKey())) {
            return translationRepo.findByKeyContainingIgnoreCase(request.getKey());
        }

        if (!StringUtils.isEmpty(request.getContent())) {
            return translationRepo.findByContentContainingIgnoreCase(request.getContent());
        }

        if (!request.getTags().isEmpty()) {
            return translationRepo.findByTagNamesOr(request.getTags());
        }

        return Collections.emptyList();
    }

    public ExportResponse exportTranslations() {
        List<Translation> translations = translationRepo.findAll();

        return ExportResponse.builder()
                .translations(translations)
                .totalCount(translations.size())
                .build();
    }
}
