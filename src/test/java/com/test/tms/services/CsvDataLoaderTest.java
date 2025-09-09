package com.test.tms.services;

import com.test.tms.entities.Translation;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.TranslationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvDataLoaderTest {
    @Mock
    private TranslationRepo translationRepo;
    @InjectMocks
    private CsvDataLoader csvDataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateSampleCsv_success() {
        String filename = csvDataLoader.generateSampleCsv(10);
        assertTrue(filename.startsWith("sample-translations-"));
        assertTrue(filename.endsWith(".csv"));
    }

    @Test
    void generateSampleCsv_tooLarge() {
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> csvDataLoader.generateSampleCsv(100001));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void loadFromMultipartFile_success() throws Exception {
        String csv = "key,locale,content,tags\napp.title.1,en,Hello,web\napp.title.2,fr,Bonjour,mobile";
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("translations.csv");
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        when(file.getInputStream()).thenReturn(is);
        doReturn(null).when(translationRepo).saveAll(anyList());
        assertDoesNotThrow(() -> csvDataLoader.loadFromMultipartFile(file));
    }

    @Test
    void loadFromMultipartFile_emptyFile() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(file.getOriginalFilename()).thenReturn("translations.csv");
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> csvDataLoader.loadFromMultipartFile(file));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void loadFromMultipartFile_notCsvFile() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("translations.txt");
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> csvDataLoader.loadFromMultipartFile(file));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void loadFromMultipartFile_internalServerError() throws Exception {
        String csv = "key,locale,content,tags\napp.title.1,en,Hello,web";
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("translations.csv");
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        when(file.getInputStream()).thenThrow(new IOException("Simulated IO error"));
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> csvDataLoader.loadFromMultipartFile(file));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatus());
        assertTrue(ex.getMessage().contains("Error loading CSV file"));
    }

    @Test
    void loadFromMultipartFile_emptyCsvContent() throws Exception {
        String csv = ""; // No content, simulates empty CSV file
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("translations.csv");
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        when(file.getInputStream()).thenReturn(is);
        // Should not throw, just log and return
        assertDoesNotThrow(() -> csvDataLoader.loadFromMultipartFile(file));
        // Optionally, verify that no saveAll is called
        verify(translationRepo, never()).saveAll(anyList());
    }

    @Test
    void loadFromMultipartFile_performanceTest() throws Exception {
        int rowCount = 10000;
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("key,locale,content,tags\n");
        for (int i = 0; i < rowCount; i++) {
            csvBuilder.append(String.format("app.title.%d,en,Hello %d,web\n", i, i));
        }
        String csv = csvBuilder.toString();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("translations.csv");
        InputStream is = new ByteArrayInputStream(csv.getBytes());
        when(file.getInputStream()).thenReturn(is);
        doReturn(null).when(translationRepo).saveAll(anyList());

        long start = System.nanoTime();
        assertDoesNotThrow(() -> csvDataLoader.loadFromMultipartFile(file));
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;
        System.out.println("Performance test duration: " + durationMs + " ms");
        // Assert that it completes within 5000ms (5 seconds)
        assertTrue(durationMs < 5000, "Performance test exceeded time threshold");
        // Verify saveAll called at least 10 times (every 1000 rows)
        verify(translationRepo, atLeast(rowCount / 1000)).saveAll(anyList());
    }
}
