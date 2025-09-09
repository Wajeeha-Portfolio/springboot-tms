package com.test.tms.services;

import com.test.tms.entities.Translation;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.TranslationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsvDataLoader {
    @Autowired
    private TranslationRepo repository;

    public String generateSampleCsv(int recordCount) {

        if (recordCount > 100000) {
            log.error("Requested record count {} exceeds maximum limit", recordCount);
            throw new CommonServiceException(HttpStatus.BAD_REQUEST, "Record count too large. Max is 100000");
        }

        String filename = "sample-translations-" + System.currentTimeMillis() + ".csv";

        String[] locales = {"en", "fr", "es", "de", "it"};
        String[] keyPrefixes = {"app", "page", "component", "error", "message"};
        String[] keySuffixes = {"title", "description", "button", "label", "error"};
        String[] tags = {"web", "mobile", "desktop", "api"};

        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("key,locale,content,tags\n");

            Random random = new Random();

            for (int i = 0; i < recordCount; i++) {
                String key = keyPrefixes[random.nextInt(keyPrefixes.length)] + "." +
                        keySuffixes[random.nextInt(keySuffixes.length)] + "." + (i % 1000);
                String locale = locales[random.nextInt(locales.length)];
                String content = "Sample content for " + key + " in " + locale + " " + i;

                // Generate 1-3 random tags
                int numTags = random.nextInt(3) + 1;
                Set<String> selectedTags = new HashSet<>();
                for (int j = 0; j < numTags; j++) {
                    selectedTags.add(tags[random.nextInt(tags.length)]);
                }
                String tagsStr = String.join(",", selectedTags);

                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        key, locale, content, tagsStr));
            }
            log.info("Sample CSV file generated: {}", filename);
            return filename;
        } catch (IOException e) {
            log.error("Error generating sample CSV file", e);
            throw new  CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating sample CSV file");
        }
    }

    @Transactional
    public void loadFromMultipartFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("File is empty");
            throw  new CommonServiceException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            log.error("File is not a CSV file");
            throw   new CommonServiceException(HttpStatus.BAD_REQUEST, "File is not a CSV file");
        }

        log.info("Loading translations from uploaded CSV file: {}", file.getOriginalFilename());

        try (InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(inputStreamReader)) {
            loadFromCsvReader(csvReader);
        }

        catch (Exception e) {
            log.error("Error loading CSV file", e);
            throw new CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading CSV file: " + e.getMessage());
        }
    }

    private void loadFromCsvReader(CSVReader csvReader) throws IOException, CsvException {
        List<String[]> records = csvReader.readAll();

        if (records.isEmpty()) {
            log.error("CSV file is empty");
            return;
        }

        String[] header = records.get(0);

        // Process data rows
        List<Translation> translations = new ArrayList<>();
        Set<String> duplicateCheck = new HashSet<>();

        for (int i = 1; i < records.size(); i++) {
            String[] row = records.get(i);

            try {
                Translation translation = parseTranslationFromRow(row, i + 1);
                String duplicateKey = translation.getKey() + ":" + translation.getLocale();

                if (duplicateCheck.contains(duplicateKey)) {
                    log.warn("Duplicate translation found at row {}: key={}, locale={}",
                            i + 1, translation.getKey(), translation.getLocale());
                    continue;
                }

                duplicateCheck.add(duplicateKey);
                translations.add(translation);

                // Batch save every 1000 records
                if (translations.size() >= 1000) {
                    saveBatch(translations);
                    translations.clear();
                }

            } catch (Exception e) {
                log.error("Error processing row {}: {}", i + 1, e.getMessage());
            }
        }

        // Save remaining translations
        if (!translations.isEmpty()) {
            saveBatch(translations);
        }

        log.info("CSV import completed. Processed {} rows", records.size() - 1);
    }

    private Translation parseTranslationFromRow(String[] row, int rowNumber) {
        if (row.length < 4) {
            throw new IllegalArgumentException("Insufficient columns in row " + rowNumber);
        }

        String key = row[0].trim();
        String locale = row[1].trim();
        String content = row[2].trim();
        String tagsStr = row[3].trim();

        if (key.isEmpty() || locale.isEmpty()) {
            throw new IllegalArgumentException("Key and locale cannot be empty in row " + rowNumber);
        }

        // Parse tags
        Set<String> tags = new HashSet<>();
        if (!tagsStr.isEmpty()) {
            tags = Arrays.stream(tagsStr.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toSet());
        }

        return Translation.builder()
                .key(key)
                .locale(locale)
                .content(content)
                .tags(tags)
                .build();
    }

    private void saveBatch(List<Translation> translations) {
            repository.saveAll(translations);
            log.info("Saved batch of {} translations", translations.size());
    }
}
