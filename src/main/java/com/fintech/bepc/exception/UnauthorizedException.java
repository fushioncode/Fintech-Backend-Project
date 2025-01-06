package com.fintech.bepc.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnauthorizedException extends RuntimeException{
    private String message;
}

