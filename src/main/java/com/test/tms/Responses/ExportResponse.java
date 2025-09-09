package com.test.tms.Responses;

import com.test.tms.entities.Translation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {
    private List<Translation> translations;
    private int totalCount;
}
