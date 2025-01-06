package com.fintech.bepc.services.serviceImpl;

import com.fintech.bepc.model.dtos.ApiResponse;
import com.fintech.bepc.model.dtos.UserRequestDto;
import com.fintech.bepc.model.dtos.UserResponseDto;
import com.fintech.bepc.model.entities.User;
import com.fintech.bepc.repositories.UserRepository;
import com.fintech.bepc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class IUserService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(IUserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public ApiResponse<UserResponseDto> getUser(Long id) {
        return ApiResponse.success(userRepository.findById(id).orElseThrow(() -> {
            logger.error("User with id {} not found", id);
            return new IllegalArgumentException("User not found");
        }).mapToDto());
    }

    @Override
    public ApiResponse<UserResponseDto> updateUser(Long id, UserRequestDto user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> {
                    logger.error("User with id {} not found", id);
                    return new IllegalArgumentException("User not found");
                });
        existingUser.setEmail(user.getEmail()!=null? user.getEmail() : existingUser.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber()!=null? user.getPhoneNumber() : existingUser.getPhoneNumber());
        existingUser.setFullName(user.getFullName()!=null? user.getFullName() : existingUser.getFullName());
        existingUser.setPassword(user.getPassword()!=null?passwordEncoder.encode(user.getPassword()): existingUser.getPassword());
        final var userData =  userRepository.save(existingUser);
        return ApiResponse.success(userData.mapToDto());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("User with id {} does not exist", id);
            throw new IllegalArgumentException("User does not exist");
        }
        userRepository.deleteById(id);
    }

    @Override
    public <T> ApiResponse<T> getAllUsers() {
        final var users = userRepository.findAll();
        return ApiResponse.success((T)users.stream().map(User::mapToDto).toList());
    }

    @Override
    public void activeAccount(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User with id {} not found", id);
            return new IllegalArgumentException("User not found");
        });

        existingUser.setActive(true);
        userRepository.save(existingUser);
    }
}
