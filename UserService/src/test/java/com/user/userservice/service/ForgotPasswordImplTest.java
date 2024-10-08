package com.user.userservice.service;

import com.user.userservice.repository.UserRepository;
import com.user.userservice.entity.User;
import com.user.userservice.exception.EmailNotFoundException;
import com.user.userservice.service.implementation.ForgotPasswordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class ForgotPasswordImplTest{

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private ForgotPasswordServiceImpl forgotPasswordService;

        private User user;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);  // Add this line
            user = new User();
            user.setEmail("test@example.com");
            user.setPassword("oldPassword");
        }

        @Test
        void updatePassword_ShouldUpdatePassword_WhenEmailExists() throws EmailNotFoundException {
            String newPassword = "newPassword";
            String encodedPassword = "encodedNewPassword";
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenReturn(user);
            boolean result = forgotPasswordService.updatePassword(user.getEmail(), newPassword);
            assertTrue(result);
            assertEquals(encodedPassword, user.getPassword());
            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(passwordEncoder, times(1)).encode(newPassword);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void updatePassword_ShouldThrowEmailNotFoundException_WhenEmailDoesNotExist() {
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
            assertThrows(EmailNotFoundException.class, () -> forgotPasswordService.updatePassword(user.getEmail(), "newPassword"));
            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(passwordEncoder, times(0)).encode(anyString());
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        void isEmailPresent_ShouldReturnTrue_WhenEmailExists() throws EmailNotFoundException {
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            boolean result = forgotPasswordService.isEmailPresent(user.getEmail());
            assertTrue(result);
            verify(userRepository, times(1)).findByEmail(user.getEmail());
        }

        @Test
        void isEmailPresent_ShouldThrowEmailNotFoundException_WhenEmailDoesNotExist() {
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
            assertThrows(EmailNotFoundException.class, () -> forgotPasswordService.isEmailPresent(user.getEmail()));
            verify(userRepository, times(1)).findByEmail(user.getEmail());
        }
    }
