package com.test.tms.controllers;

import com.test.tms.Responses.ExportResponse;
import com.test.tms.Responses.SuccessResponse;
import com.test.tms.entities.Translation;
import com.test.tms.requests.SearchRequest;
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
        Long id = translationService.addTranslation(request);
        return ResponseEntity.ok(new SuccessResponse("Translation added successfully with ID: " + id));
    }

    // Update translation
    @PutMapping("/translation/{id}")
    public ResponseEntity<?> updateTranslation(@PathVariable(required = true) Long id,
                                               @RequestBody TranslationRequest request) {
        translationService.updateTranslation(id, request);
        return ResponseEntity.ok(new SuccessResponse("Translation updated successfully"));
    }

     // View translation by ID
    @GetMapping("/translation/{id}")
    public Translation viewTranslation(@PathVariable Long id) {
        return translationService.getTranslationById(id);
    }

    // Search translations by key, lang, or context
    @PostMapping("/translation/search")
    public List<Translation> searchTranslations(@RequestBody SearchRequest request) {
        return translationService.searchTranslations(request);
    }

    @GetMapping("/translation/export")
    public ResponseEntity<ExportResponse> exportTranslations() {
        ExportResponse response = translationService.exportTranslations();
        return ResponseEntity.ok(response);
    }
}
