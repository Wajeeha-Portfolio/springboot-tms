package com.test.tms.controllers;

import com.test.tms.Responses.ExportResponse;
import com.test.tms.Responses.SuccessResponse;
import com.test.tms.entities.Translation;
import com.test.tms.requests.SearchRequest;
import com.test.tms.requests.TranslationRequest;
import com.test.tms.services.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Translation Management", description = "Endpoints for managing translations: add, update, view, search, and export.")
@RestController
public class TranslationController {
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Add a new translation.
     * @param request TranslationRequest containing translation details
     * @return SuccessResponse with the new translation ID
     */
    @Operation(summary = "Add translation", description = "Adds a new translation record.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/translation")
    public ResponseEntity<?> addTranslation(@RequestBody TranslationRequest request) {
        Long id = translationService.addTranslation(request);
        return ResponseEntity.ok(new SuccessResponse("Translation added successfully with ID: " + id));
    }

    /**
     * Update an existing translation by ID.
     * @param id Translation ID
     * @param request TranslationRequest with updated details
     * @return SuccessResponse
     */
    @Operation(summary = "Update translation", description = "Updates an existing translation record by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation updated successfully"),
        @ApiResponse(responseCode = "404", description = "Translation not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/translation/{id}")
    public ResponseEntity<?> updateTranslation(
            @Parameter(description = "Translation ID", required = true)
            @PathVariable(required = true) Long id,
            @RequestBody TranslationRequest request) {
        translationService.updateTranslation(id, request);
        return ResponseEntity.ok(new SuccessResponse("Translation updated successfully"));
    }

    /**
     * View a translation by ID.
     * @param id Translation ID
     * @return Translation object
     */
    @Operation(summary = "View translation", description = "Retrieves a translation record by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Translation found"),
        @ApiResponse(responseCode = "404", description = "Translation not found")
    })
    @GetMapping("/translation/{id}")
    public Translation viewTranslation(
            @Parameter(description = "Translation ID", required = true)
            @PathVariable Long id) {
        return translationService.getTranslationById(id);
    }

    /**
     * Search translations by key, content, or tags.
     * @param request SearchRequest with search parameters
     * @return List of matching Translation objects
     */
    @Operation(summary = "Search translations", description = "Searches for translations by key, content, or tags.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search successful"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @PostMapping("/translation/search")
    public List<Translation> searchTranslations(@RequestBody SearchRequest request) {
        return translationService.searchTranslations(request);
    }

    /**
     * Export all translations.
     * @return ExportResponse containing all translations and metadata
     */
    @Operation(summary = "Export translations", description = "Exports all translations as a list.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export successful")
    })
    @GetMapping("/translation/export")
    public ResponseEntity<ExportResponse> exportTranslations() {
        ExportResponse response = translationService.exportTranslations();
        return ResponseEntity.ok(response);
    }
}
