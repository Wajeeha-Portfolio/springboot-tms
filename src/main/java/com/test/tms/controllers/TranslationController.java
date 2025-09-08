package com.test.tms.controllers;

import com.test.tms.entities.Translation;
import com.test.tms.requests.TranslationRequest;
import com.test.tms.services.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TranslationController {
    @Autowired
    TranslationService translationService;

    @PostMapping("/translation")
    public ResponseEntity<?> addTranslation(@RequestBody TranslationRequest request) {
        translationService.addTranslation(request.getField(), request.getContent(), request.getLocale(),
                request.getTags());
        return ResponseEntity.ok().build();
    }

    // Update translation
    @PutMapping("/translation/{id}")
    public ResponseEntity<?> updateTranslation(@PathVariable(required = true) Long id,
                                               @RequestBody TranslationRequest request) {
        translationService.updateTranslation(id, request.getField(), request.getContent(), request.getLocale(),
                request.getTags());
        return ResponseEntity.ok().build();
    }

     // View translation by ID
    @GetMapping("/translation/{id}")
    public Translation viewTranslation(@PathVariable Long id) {
        return translationService.getTranslationById(id);
    }

    // Search translations by key, lang, or context
    @GetMapping("/translation/search")
    public List<Translation> searchTranslations(@RequestParam(required = false) String field,
                                                @RequestParam(required = false) String lang,
                                                @RequestParam(required = false) String locale) {
        return translationService.searchTranslations(field, lang, locale);

    }
}
