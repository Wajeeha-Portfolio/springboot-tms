package com.test.tms.requests;

import lombok.Data;

import java.util.Set;

@Data
public class TranslationRequest {
    private String key;
    private String content;
    private Set<String> tags;
    private String locale;
}

