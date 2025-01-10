package com.fintech.bepc.model.dtos;

import com.fintech.bepc.custom.annotations.ValidEmail;
import com.fintech.bepc.custom.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}



