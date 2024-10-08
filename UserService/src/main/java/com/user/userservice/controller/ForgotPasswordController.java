package com.user.userservice.controller;

import com.user.userservice.dto.ApiResponse;
import com.user.userservice.dto.ForgotPasswordDTO;
import com.user.userservice.dto.ResetPasswordDTO;
import com.user.userservice.exception.EmailNotFoundException;
import com.user.userservice.exception.PasswordMismatchException;
import com.user.userservice.service.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@Data
@RequiredArgsConstructor
@RequestMapping("/users")
public class ForgotPasswordController {
    private  final ForgotPasswordService forgotPasswordService;
    private String userEmail;

    @PostMapping("/validate-email")
    public ResponseEntity<ApiResponse> validateEmail(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) throws EmailNotFoundException{
        String email= forgotPasswordDTO.getEmail();
        forgotPasswordService.isEmailPresent(email);
        userEmail=email;
        ApiResponse response=ApiResponse.builder()
                .response("Email is present, please change your password.")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO)throws EmailNotFoundException,PasswordMismatchException {
        String email= resetPasswordDTO.getEmail();
        String newPassword = resetPasswordDTO.getNewPassword();
        String confirmPassword = resetPasswordDTO.getConfirmPassword();
        try {
            forgotPasswordService.validateResetPasswordRequest(userEmail, email, newPassword, confirmPassword);
        } catch (PasswordMismatchException e) {
            ApiResponse response = ApiResponse.builder()
                    .response(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        boolean isUpdated = forgotPasswordService.updatePassword(email, newPassword);
        String message=isUpdated?"Password has been reset successfully.":"Error resetting password.";
        ApiResponse response=ApiResponse.builder()
                .response(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(isUpdated?HttpStatus.OK:HttpStatus.BAD_REQUEST).body(response);
    }
}