package com.test.tms.services;

import com.test.tms.entities.Translation;
import com.test.tms.repositories.TranslationRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TranslationService {
    @Autowired
    TranslationRepo translationRepo;

    public void addTranslation(String field, String content, String locale, String tags) {
        // Implementation to add translation
        if (StringUtils.isEmpty(field) || StringUtils.isEmpty(content) || StringUtils.isEmpty(locale)
                || StringUtils.isEmpty(tags)) {
//            System.console().print("Key, content, locale, and tags must not be empty");
            throw new IllegalArgumentException("Invalid input");
        }

        Translation t = Translation.builder().field(field).content(content).locale(locale)
                .tags(Arrays.asList(tags.split(","))).build();
        translationRepo.save(t);
    }

    public void loadData() {

    }

    public void updateTranslation(Long id, String key, String value, String lang, String context) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || StringUtils.isEmpty(lang) || StringUtils.isEmpty(context)) {
            throw new IllegalArgumentException("Invalid input");
        }
        Optional<Translation> opt = translationRepo.findById(id);
        if (opt.isPresent()) {
            Translation t = opt.get();
            t.setField(key);
            t.setContent(value);
            t.setLocale(lang);
            t.setTags(Arrays.asList(context));
            translationRepo.save(t);
        } else {
            throw new IllegalArgumentException("Translation not found");
        }
    }

    public Translation getTranslationById(Long id) {
        Optional<Translation> opt = translationRepo.findById(id);
        return opt.orElse(null);
    }

    public List<Translation> searchTranslations(String field, String content, String tag) {
        return translationRepo.searchAllByFieldOrContent(field, content);
    }
}
