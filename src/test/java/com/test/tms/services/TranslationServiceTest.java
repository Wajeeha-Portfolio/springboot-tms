package com.test.tms.services;

import com.test.tms.Responses.ExportResponse;
import com.test.tms.entities.Translation;
import com.test.tms.exception.CommonServiceException;
import com.test.tms.repositories.TranslationRepo;
import com.test.tms.requests.SearchRequest;
import com.test.tms.requests.TranslationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationServiceTest {
    @Mock
    private TranslationRepo translationRepo;
    @InjectMocks
    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTranslation_success() {
        TranslationRequest request = new TranslationRequest();
        request.setKey("greeting");
        request.setContent("Hello");
        request.setLocale("en");
        request.setTags(new HashSet<>(Arrays.asList("web")));
        Translation translation = Translation.builder()
                .key("greeting")
                .content("Hello")
                .locale("en")
                .tags(request.getTags())
                .build();
        translation.setId(1L);
        when(translationRepo.save(any(Translation.class))).thenAnswer(invocation -> {
            Translation t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });
        Long id = translationService.addTranslation(request);
        assertEquals(1L, id);
    }

    @Test
    void addTranslation_invalidInput() {
        TranslationRequest request = new TranslationRequest();
        request.setKey("");
        request.setContent("");
        request.setLocale("");
        request.setTags(new HashSet<>());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.addTranslation(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void addTranslation_invalidInput_allFields() {
        TranslationRequest request = new TranslationRequest();
        request.setKey(""); // Empty key
        request.setContent(""); // Empty content
        request.setLocale(""); // Empty locale
        request.setTags(new HashSet<>()); // Empty tags
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.addTranslation(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Invalid input", ex.getMessage());
    }

    @Test
    void updateTranslation_success() {
        TranslationRequest request = new TranslationRequest();
        request.setKey("greeting");
        request.setContent("Hi");
        request.setLocale("en");
        request.setTags(new HashSet<>(Arrays.asList("web")));
        Translation translation = Translation.builder()
                .id(1L)
                .key("greeting")
                .content("Hello")
                .locale("en")
                .tags(new HashSet<>(Arrays.asList("web")))
                .build();
        when(translationRepo.findById(1L)).thenReturn(Optional.of(translation));
        when(translationRepo.save(any(Translation.class))).thenReturn(translation);
        assertDoesNotThrow(() -> translationService.updateTranslation(1L, request));
        assertEquals("Hi", translation.getContent());
    }

    @Test
    void updateTranslation_notFound() {
        TranslationRequest request = new TranslationRequest();
        request.setKey("greeting");
        request.setContent("Hi");
        request.setLocale("en");
        request.setTags(new HashSet<>(Arrays.asList("web")));
        when(translationRepo.findById(1L)).thenReturn(Optional.empty());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.updateTranslation(1L, request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateTranslation_badRequest() {
        TranslationRequest request = new TranslationRequest();
        request.setKey("");
        request.setContent("");
        request.setLocale("");
        request.setTags(new HashSet<>(Arrays.asList("")));
        when(translationRepo.findById(1L)).thenReturn(Optional.empty());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.updateTranslation(1L, request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void getTranslationById_success() {
        Translation translation = Translation.builder().id(1L).key("greeting").content("Hello").locale("en").tags(new HashSet<>(Arrays.asList("web"))).build();
        when(translationRepo.findById(1L)).thenReturn(Optional.of(translation));
        Translation result = translationService.getTranslationById(1L);
        assertEquals("greeting", result.getKey());
    }

    @Test
    void getTranslationById_notFound() {
        when(translationRepo.findById(1L)).thenReturn(Optional.empty());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.getTranslationById(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void searchTranslations_byKey() {
        SearchRequest request = new SearchRequest();
        request.setKey("greet");
        request.setContent("");
        request.setTags(new HashSet<>());
        List<Translation> translations = Arrays.asList(Translation.builder().key("greeting").build());
        when(translationRepo.findByKeyContainingIgnoreCase("greet")).thenReturn(translations);
        List<Translation> result = translationService.searchTranslations(request);
        assertEquals(1, result.size());
    }

    @Test
    void searchTranslations_invalidInput() {
        SearchRequest request = new SearchRequest();
        request.setKey("");
        request.setContent("");
        request.setTags(new HashSet<>());
        CommonServiceException ex = assertThrows(CommonServiceException.class, () -> translationService.searchTranslations(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void exportTranslations_success() {
        List<Translation> translations = Arrays.asList(Translation.builder().id(1L).key("greeting").build());
        when(translationRepo.findAll()).thenReturn(translations);
        ExportResponse response = translationService.exportTranslations();
        assertEquals(1, response.getTotalCount());
        assertEquals(translations, response.getTranslations());
    }

    @Test
    void searchTranslations_byContent() {
        SearchRequest request = new SearchRequest();
        request.setKey("");
        request.setContent("Hello");
        request.setTags(new HashSet<>());
        List<Translation> translations = Arrays.asList(Translation.builder().content("Hello").build());
        when(translationRepo.findByContentContainingIgnoreCase("Hello")).thenReturn(translations);
        List<Translation> result = translationService.searchTranslations(request);
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getContent());
    }

    @Test
    void searchTranslations_byTags() {
        SearchRequest request = new SearchRequest();
        request.setKey("");
        request.setContent("");
        request.setTags(new HashSet<>(Arrays.asList("web")));
        List<Translation> translations = Arrays.asList(Translation.builder().tags(new HashSet<>(Arrays.asList("web"))).build());
        when(translationRepo.findByTagNamesOr(request.getTags())).thenReturn(translations);
        List<Translation> result = translationService.searchTranslations(request);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTags().contains("web"));
    }

    @Test
    void searchTranslations_byKeyContentTags() {
        SearchRequest request = new SearchRequest();
        request.setKey("greet");
        request.setContent("Hello");
        request.setTags(new HashSet<>(Arrays.asList("web")));
        List<Translation> translations = Arrays.asList(Translation.builder().key("greet").content("Hello").tags(request.getTags()).build());
        when(translationRepo.searchByKeyOrContentWithTags("greet", "Hello", request.getTags())).thenReturn(translations);
        List<Translation> result = translationService.searchTranslations(request);
        assertEquals(1, result.size());
        assertEquals("greet", result.get(0).getKey());
        assertEquals("Hello", result.get(0).getContent());
        assertTrue(result.get(0).getTags().contains("web"));
    }

    @Test
    void searchTranslations_emptyResult() {
        SearchRequest request = new SearchRequest();
        request.setKey("data-not-present");
        request.setContent("");
        request.setTags(new HashSet<>());
        when(translationRepo.findByKeyContainingIgnoreCase("")).thenReturn(Collections.emptyList());
        when(translationRepo.findByContentContainingIgnoreCase("")).thenReturn(Collections.emptyList());
        when(translationRepo.findByTagNamesOr(request.getTags())).thenReturn(Collections.emptyList());
        List<Translation> result = translationService.searchTranslations(request);
        assertTrue(result.isEmpty());
    }
}
