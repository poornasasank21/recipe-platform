package com.user.userservice.service;

import com.user.userservice.exception.EmailNotFoundException;
import com.user.userservice.exception.PasswordMismatchException;
public interface ForgotPasswordService {
    boolean updatePassword(String userEmail, String newPassword) throws EmailNotFoundException;
    boolean isEmailPresent(String email) throws EmailNotFoundException;
    boolean validateResetPasswordRequest(String email, String requestEmail, String newPassword, String confirmPassword) throws EmailNotFoundException, PasswordMismatchException;
}
