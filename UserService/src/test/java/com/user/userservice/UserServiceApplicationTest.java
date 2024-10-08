package com.user.userservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceApplicationTest {
    @Test
    @DisplayName("Main Method Test")
    void main() {
        assertDoesNotThrow(() -> UserServiceApplication.main(new String[]{}));
    }
}