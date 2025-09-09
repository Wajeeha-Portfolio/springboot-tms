package com.test.tms.controllers;

/**
 * Controller for test and utility endpoints related to CSV translation data.
 * Provides endpoints to generate sample CSV files and import translation data from CSV files.
 * Intended for development, testing, and bulk data operations.
 */
@RestController
public class TestDataLoader {
    @Autowired
    private CsvDataLoader csvLoader;

    /**
     * Generates a sample CSV file with translation records for testing or bulk import.
     *
     * @param count Number of records to generate (default: 1000)
     * @return ResponseEntity containing a message, file path, and record count
     *
     * Example request: GET /sample-csv?count=5000
     * Example response:
     * {
     *   "message": "Sample CSV generated",
     *   "filepath": "sample-translations-1757363242700.csv",
     *   "recordCount": "5000"
     * }
     */
    @GetMapping("/sample-csv")
    public ResponseEntity<Map<String, String>> generateSampleCsv(@RequestParam(defaultValue = "1000") int count) {
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
     *
     * Example request: POST /csv (form-data: file=<yourfile.csv>)
     * Example response:
     * {
     *   "message": "CSV file imported successfully",
     *   "filename": "yourfile.csv"
     * }
     */
    @PostMapping("/csv")
    public ResponseEntity<Map<String, String>> importCsvFile(@RequestParam("file") MultipartFile file) {
        csvLoader.loadFromMultipartFile(file);
        return ResponseEntity.ok(Map.of(
                "message", "CSV file imported successfully",
                "filename", file.getOriginalFilename()
        ));
    }
}