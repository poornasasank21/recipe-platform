package com.user.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;
    @InjectMocks
    private TokenService tokenService;
    private Authentication authentication;
    @BeforeEach
    void setUp() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        User principal = new User("test@example.com", "password", authorities);
        authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Test
    void shouldGenerateToken_WhenCalled_ReturnsValidToken() {
        Instant now = Instant.now();
        String expectedToken = "mock.jwt.token";
        Jwt jwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "none")
                .claim("scope", "ROLE_USER")
                .claim("exp", now.plusSeconds(7200))
                .claim("iss", "Epam System Inc.")
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        String token = tokenService.generateToken(authentication);
        assertEquals(expectedToken, token);
    }
}