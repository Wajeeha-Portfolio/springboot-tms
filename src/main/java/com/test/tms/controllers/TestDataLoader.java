package com.test.tms.controllers;

import com.test.tms.services.CsvDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class TestDataLoader {
    @Autowired
    private CsvDataLoader csvLoader;

    @GetMapping("/sample-csv")
    public ResponseEntity<Map<String, String>> generateSampleCsv(@RequestParam(defaultValue = "1000") int count) {
        String filepath = csvLoader.generateSampleCsv(count);
        return ResponseEntity.ok(Map.of(
                "message", "Sample CSV generated",
                "filepath", filepath,
                "recordCount", String.valueOf(count)
        ));
    }

    @PostMapping("/csv")
    public ResponseEntity<Map<String, String>> importCsvFile(@RequestParam("file") MultipartFile file) {
        csvLoader.loadFromMultipartFile(file);
        return ResponseEntity.ok(Map.of(
                "message", "CSV file imported successfully",
                "filename", file.getOriginalFilename()
        ));
    }
}