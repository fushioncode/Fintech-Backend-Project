package com.fintech.bepc.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnauthorizedException extends RuntimeException{
    private String message;
}

