package com.test.tms.requests;

import lombok.Data;

import java.util.Set;

@Data
public class SearchRequest {
    private String key;
    private String content;
    private Set<String> tags;
}
