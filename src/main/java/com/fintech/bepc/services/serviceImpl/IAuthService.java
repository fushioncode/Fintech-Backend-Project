package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.exception.UnauthorizedException;
import com.fintech.bepc.model.dtos.AuthRequestDto;
import com.fintech.bepc.model.dtos.AuthResponseDto;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.UserRepository;
import com.fintech.bepc.services.security.CustomUserDetailsService;
import com.fintech.bepc.services.security.JwtTokenProvider;
import com.fintech.bepc.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAuthService implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        UserDetails user = customUserDetailsService.loadUserByUsername(authRequestDto.getEmail());
        try{
            authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword()), user);
        } catch (Exception e) {
            log.error("AUTHENTICATION ERROR::::::::", e);
            throw new IllegalArgumentException("Invalid Username and Password");
        }

        final var tokenData = jwtTokenProvider.generateToken(user);
        return tokenData;
    }


    @Override
    public void registerUser(UserRequestDto authRequestDto) {
        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(authRequestDto.getEmail());
        user.setPhoneNumber(authRequestDto.getPhoneNumber());
        user.setFullName(authRequestDto.getFullName());
        String hashedPassword = passwordEncoder.encode(authRequestDto.getPassword());
        log.info("Password hash: {}", hashedPassword);
        user.setPassword(hashedPassword);
        log.info("Hash password after setting: {}", hashedPassword);
        user.setRole(User.Role.USER);
        var newUser = userRepository.save(user);
        log.info("Hashed password after save: {}", newUser.getPassword());
    }

    @Override
    public void registerAdmin(UserRequestDto authRequestDto) {
        if (userRepository.existsByEmail(authRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(authRequestDto.getEmail());
        user.setPhoneNumber(authRequestDto.getPhoneNumber());
        user.setFullName(authRequestDto.getFullName());
        user.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        user.setRole(User.Role.ADMIN);
        userRepository.save(user);
    }

    private Authentication authenticate(Authentication authentication, UserDetails user){
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        return checkPassword(user, password);
    }

    private Authentication checkPassword(UserDetails user, String rawPassword){
        if (passwordEncoder.matches(rawPassword, user.getPassword())){
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), user.getAuthorities());
        }else{
            throw new UnauthorizedException("Bad credentials");
        }
    }
}

