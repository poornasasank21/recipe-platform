package com.user.userservice.service;

import com.user.userservice.entity.User;
import com.user.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService userDetailsService;
    private final String email = "test@example.com";

    @Test
    void testLoadUserByUsername_UserNull_ThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    void testLoadUserByUsername_UserNotNullButDisabled_ThrowsUsernameNotFoundException() {
        User user = new User();
        user.setEnabled(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    void testLoadUserByUsername_UserNotNullAndEnabled_ReturnsUser() {
        User user = new User();
        user.setEnabled(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        User result = userDetailsService.loadUserByUsername(email);
        assertNotNull(result);
        assertTrue(result.isEnabled());
    }
}