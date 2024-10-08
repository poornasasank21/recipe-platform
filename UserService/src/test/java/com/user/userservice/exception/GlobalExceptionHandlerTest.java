package com.user.userservice.exception;

import com.user.userservice.constants.ErrorConstants;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private HttpServletRequest request;
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleUsernameNotFoundException() {
        UsernameNotFoundException exception = new UsernameNotFoundException(ErrorConstants.USER_NOT_FOUND);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUsernameNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
    }

    @Test
    void shouldHandleIncorrectPasswordException() {
        IncorrectPasswordException exception = new IncorrectPasswordException(ErrorConstants.INVALID_PASSWORD);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidPasswordException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Password", response.getBody().getError());
    }

    @Test
    void shouldHandleJwtException() {
        JwtException exception = new JwtException("Invalid JWT");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleJwtException(exception, request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid JWT", response.getBody().getError());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(exception);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody().getError());
        assertEquals("Access denied", response.getBody().getError());
    }
}