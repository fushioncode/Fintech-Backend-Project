package com.fintech.bepc.model.entities;

import com.fintech.bepc.model.dtos.UserResponseDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String fullName;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isActive;

    public enum Role {
        ADMIN, USER
    }

    public void setPassword(String password) {
        System.out.println("Setting password: " + password);
        if (password != null && !password.startsWith("$2a$")) {
            this.password = new BCryptPasswordEncoder().encode(password);
        } else {
            this.password = password;
        }
    }

    public UserResponseDto mapToDto(){
        return new UserResponseDto(this.id, this.email, this.fullName, this.phoneNumber);
    }
}
