package com.test.tms.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CommonServiceException extends RuntimeException {
    private final HttpStatus status;
    private final String endUserMessage;

    public CommonServiceException(HttpStatus status, String endUserMessage) {
        super(endUserMessage);
        this.status = status;
        this.endUserMessage = endUserMessage;
    }
}
