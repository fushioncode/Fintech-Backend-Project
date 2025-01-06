package com.fintech.bepc.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class APIException extends RuntimeException{
    private final String message;
    private final int statusCode;
    @Builder
    public APIException(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
