package com.user.userservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(UserSecurityConfiguration.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=none"})
class UserSecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;
    private JwtDecoder jwtDecoder;
    @Autowired
    private UserSecurityConfiguration securityConfig;
    @BeforeEach
    void setup() {
        jwtDecoder = mock(JwtDecoder.class);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user")
                .claim("scope", "ROLE_USER")
                .build();
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(jwt.getSubject(), "credentials", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void jwtEncoderBeanShouldBeCreated() {
        JwtEncoder jwtEncoder = securityConfig.jwtEncoder();
        assertNotNull(jwtEncoder, "JwtEncoder should not be null");
    }

    @Test
    void jwtDecoderBeanShouldBeCreated() {
        jwtDecoder = securityConfig.jwtDecoder();
        assertNotNull(jwtDecoder, "JwtDecoder should not be null");
    }

    @Test
    void passwordEncoderBeanShouldBeCreated() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder, "PasswordEncoder should not be null");
    }

    @Test
    void authenticationManagerBeanShouldBeCreated() {
        AuthenticationManager authenticationManager = securityConfig.authenticationManager();
        assertNotNull(authenticationManager, "AuthenticationManager should not be null");
    }

    @Test
    void jwtAuthenticationConverterShouldBeConfigured() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        assertNotNull(converter, "JwtAuthenticationConverter should not be null");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void shouldForbidAccessToAdminEndpointForUserRole() throws Exception {
        mockMvc.perform(get("/admins/some-action"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldAuthenticateAnySecuredRequest() throws Exception {
        mockMvc.perform(get("/users/update/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/users/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Logged out successfully \"}"));
    }
}