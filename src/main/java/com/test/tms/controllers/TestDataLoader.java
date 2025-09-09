package com.test.tms.controllers;

import com.test.tms.services.CsvDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class TestDataLoader {
    @Autowired
    private CsvDataLoader csvLoader;

    @GetMapping("/sample-csv")
    public ResponseEntity<Map<String, String>> generateSampleCsv(
            @RequestParam(defaultValue = "1000") int count) {

        if (count > 100000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Maximum 100,000 records allowed for CSV generation"));
        }

        try {
            String filename = "sample-translations-" + System.currentTimeMillis() + ".csv";
            String filepath = filename;

            csvLoader.generateSampleCsv(filepath, count);

            return ResponseEntity.ok(Map.of(
                    "message", "Sample CSV generated",
                    "filepath", filepath,
                    "recordCount", String.valueOf(count)
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate CSV: " + e.getMessage()));
        }
    }

    @PostMapping("/csv")
    public ResponseEntity<Map<String, String>> importCsvFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File must be a CSV"));
        }

        try {
            csvLoader.loadFromMultipartFile(file);
            return ResponseEntity.ok(Map.of(
                    "message", "CSV file imported successfully",
                    "filename", file.getOriginalFilename()
            ));
        } catch (IOException | CsvException e) {
            System.console().format("Error importing CSV file", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to import CSV: " + e.getMessage()));
        }
    }
}
