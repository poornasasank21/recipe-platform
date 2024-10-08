package com.user.userservice.controller;

import com.user.userservice.dto.ApiResponse;
import com.user.userservice.dto.ForgotPasswordDTO;
import com.user.userservice.dto.ResetPasswordDTO;
import com.user.userservice.exception.EmailNotFoundException;
import com.user.userservice.exception.PasswordMismatchException;
import com.user.userservice.service.ForgotPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ForgotPasswordControllerTest {

    @Mock
    private ForgotPasswordService forgotPasswordService;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for validating email when email exists
    @Test
    void testValidateEmail_EmailExists() throws EmailNotFoundException {
        // Arrange
        String email = "test@example.com";
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail(email); // Set the email in DTO

        // Mock the forgotPasswordService to not throw exception when isEmailPresent is called
        when(forgotPasswordService.isEmailPresent(email)).thenReturn(true); // Simulating email is present

        // Act
        ResponseEntity<ApiResponse> response = forgotPasswordController.validateEmail(forgotPasswordDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email is present, please change your password.", response.getBody().getResponse());
        verify(forgotPasswordService, times(1)).isEmailPresent(email);
    }

    // Test case for validating email when email is not found
    @Test
    void testValidateEmail_EmailNotFound() throws EmailNotFoundException {
        // Arrange
        String email = "test@example.com";
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setEmail(email); // Set the email in DTO

        // Mock to throw EmailNotFoundException when the email is not found
        doThrow(new EmailNotFoundException("Email Not Found")).when(forgotPasswordService).isEmailPresent(email);

        // Act & Assert
        assertThrows(EmailNotFoundException.class, () -> forgotPasswordController.validateEmail(forgotPasswordDTO));
        verify(forgotPasswordService, times(1)).isEmailPresent(email);
    }

    // Test case for resetting password with valid inputs
    @Test
    void testResetPassword_ValidInputs() throws EmailNotFoundException, PasswordMismatchException {
        // Arrange
        String validatedEmail = "test@example.com";  // Simulating the validated email
        forgotPasswordController.setUserEmail(validatedEmail);  // Simulate that email validation was done

        // Create the ResetPasswordDTO with valid inputs
        ResetPasswordDTO passwordDTO = new ResetPasswordDTO();
        passwordDTO.setEmail(validatedEmail);
        passwordDTO.setNewPassword("NewPass@123");
        passwordDTO.setConfirmPassword("NewPass@123");

        // Mock the service to return true when updating the password
        when(forgotPasswordService.updatePassword(validatedEmail, "NewPass@123")).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse> response = forgotPasswordController.resetPassword(passwordDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has been reset successfully.", response.getBody().getResponse());
        verify(forgotPasswordService, times(1)).updatePassword(validatedEmail, "NewPass@123");
    }

    // Test case for resetting password with mismatched passwords
    @Test
    void testResetPassword_PasswordMismatch() throws EmailNotFoundException, PasswordMismatchException {
        // Arrange
        String validatedEmail = "test@example.com";  // Simulating the validated email
        forgotPasswordController.setUserEmail(validatedEmail);  // Simulate that email validation was done

        // Create ResetPasswordDTO with mismatching passwords
        ResetPasswordDTO passwordDTO = new ResetPasswordDTO();
        passwordDTO.setEmail(validatedEmail);
        passwordDTO.setNewPassword("NewPass@123");
        passwordDTO.setConfirmPassword("WrongPass@123");

        // Mock the behavior of the service to throw PasswordMismatchException when passwords don't match
        doThrow(new PasswordMismatchException("New password and confirm password do not match."))
                .when(forgotPasswordService).validateResetPasswordRequest(validatedEmail, validatedEmail, "NewPass@123", "WrongPass@123");

        // Act
        ResponseEntity<ApiResponse> response = forgotPasswordController.resetPassword(passwordDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("New password and confirm password do not match.", response.getBody().getResponse());

        // Verify that updatePassword method was never called, since passwords don't match
        verify(forgotPasswordService, never()).updatePassword(anyString(), anyString());
    }

    // Test case for failure to update the password
    @Test
    void testResetPassword_UpdatePasswordFailure() throws EmailNotFoundException, PasswordMismatchException {
        // Arrange
        String validatedEmail = "test@example.com";  // Simulating the validated email
        forgotPasswordController.setUserEmail(validatedEmail);  // Simulate that email validation was done

        // Create ResetPasswordDTO with matching new and confirm passwords
        ResetPasswordDTO passwordDTO = new ResetPasswordDTO();
        passwordDTO.setEmail(validatedEmail);
        passwordDTO.setNewPassword("NewPass@123");
        passwordDTO.setConfirmPassword("NewPass@123");

        // Simulate failure of password update
        when(forgotPasswordService.updatePassword(validatedEmail, "NewPass@123")).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse> response = forgotPasswordController.resetPassword(passwordDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error resetting password.", response.getBody().getResponse());
        verify(forgotPasswordService, times(1)).updatePassword(validatedEmail, "NewPass@123");
    }
}
