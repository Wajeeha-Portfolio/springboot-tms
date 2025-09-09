package com.test.tms.controllers;

import com.test.tms.services.CsvDataLoader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller for test and utility endpoints related to CSV translation data.
 * Provides endpoints to generate sample CSV files and import translation data from CSV files.
 * Intended for development, testing, and bulk data operations.
 */
@Tag(name = "Test Data Loader", description = "Endpoints for generating and importing translation CSV data for testing and bulk operations.")
@RestController
public class TestDataLoader {
    private final CsvDataLoader csvLoader;

    public TestDataLoader(CsvDataLoader csvLoader) {
        this.csvLoader = csvLoader;
    }

    /**
     * Generates a sample CSV file with translation records for testing or bulk import.
     *
     * @param count Number of records to generate (default: 1000)
     * @return ResponseEntity containing a message, file path, and record count
     */
    @Operation(
        summary = "Generate sample CSV file",
        description = "Generates a sample CSV file with the specified number of translation records for testing or bulk import."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sample CSV generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid record count")
    })
    @GetMapping("/sample-csv")
    public ResponseEntity<Map<String, String>> generateSampleCsv(
            @Parameter(description = "Number of records to generate", example = "1000")
            @RequestParam(defaultValue = "1000") int count) {
        String filepath = csvLoader.generateSampleCsv(count);
        return ResponseEntity.ok(Map.of(
                "message", "Sample CSV generated",
                "filepath", filepath,
                "recordCount", String.valueOf(count)
        ));
    }

    /**
     * Imports translation data from an uploaded CSV file.
     *
     * @param file MultipartFile representing the uploaded CSV file
     * @return ResponseEntity containing a message and the original filename
     */
    @Operation(
        summary = "Import translations from CSV file",
        description = "Imports translation data from an uploaded CSV file. The file must be in the correct format with columns: key, locale, content, tags."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file imported successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or empty file"),
        @ApiResponse(responseCode = "500", description = "Internal server error during import")
    })
    @PostMapping("/csv")
    public ResponseEntity<Map<String, String>> importCsvFile(
            @Parameter(description = "CSV file to import", required = true)
            @RequestParam("file") MultipartFile file) {
        csvLoader.loadFromMultipartFile(file);
        return ResponseEntity.ok(Map.of(
                "message", "CSV file imported successfully",
                "filename", file.getOriginalFilename()
        ));
    }
}