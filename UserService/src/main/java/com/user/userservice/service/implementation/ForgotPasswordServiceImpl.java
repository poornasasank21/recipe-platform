package com.user.userservice.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.userservice.exception.PasswordMismatchException;
import com.user.userservice.repository.UserRepository;
import com.user.userservice.entity.User;
import com.user.userservice.exception.EmailNotFoundException;
import com.user.userservice.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public boolean isEmailPresent(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new EmailNotFoundException("Email Not Found"));
        String existingEmail = user.getEmail();
        return existingEmail.equals(email);
    }
    @Override
    public boolean validateResetPasswordRequest(String userEmail,String email,String newPassword, String confirmPassword) throws EmailNotFoundException,PasswordMismatchException{
        if (!email.equals(userEmail)) {
            throw new EmailNotFoundException("Please provide the correct email address.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("New password and confirm password do not match.");
        }
        return true;
    }
    @Transactional
    @Override
    public boolean updatePassword(String userEmail, String newPassword) throws EmailNotFoundException {
        User userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EmailNotFoundException("Email Not Found"));
        String encodedPassword=passwordEncoder.encode(newPassword);
        userEntity.setPassword(encodedPassword);
        User savedUserEntity = userRepository.save(userEntity);
        return savedUserEntity.getPassword().equals(encodedPassword);
    }
}
