package com.user.userservice.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetAuthorities() {
        User user = new User();
        user.setRole("user");
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertFalse(authorities.isEmpty());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    @Test
    void testGetUsername() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User();
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User();
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User();
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        User user = new User();
        user.setEnabled(true);
        assertTrue(user.isEnabled());
    }
}