package com.test.tms.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    private String message;
    private boolean success;

    public SuccessResponse(String message) {
        this.message = message;
        this.success = true;
    }
}
