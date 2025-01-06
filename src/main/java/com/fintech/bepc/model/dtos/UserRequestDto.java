package com.fintech.bepc.model.dtos;

import com.fintech.bepc.custom.annotations.ValidEmail;
import com.fintech.bepc.custom.annotations.ValidPassword;
import com.fintech.bepc.custom.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;

    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @ValidPhoneNumber
    private String phoneNumber;
}
