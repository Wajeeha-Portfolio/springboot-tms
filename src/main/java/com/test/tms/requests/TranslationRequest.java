package com.test.tms.requests;

import lombok.Data;

@Data
public class TranslationRequest {
    private String field;
    private String content;
    private String tags;
    private String locale;
}

