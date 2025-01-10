package com.fintech.bepc.model.dtos;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String token;
    private Date tokenExpiry;
    private Date refreshTokenExpiry;
    private String refreshToken;
}
