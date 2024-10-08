package com.user.userservice.exception;

import com.user.userservice.constants.ErrorConstants;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;
import java.time.LocalDateTime;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        if (authException.getCause() instanceof ExpiredJwtException) {
            ErrorResponse errorResponse= ErrorResponse.builder()
                    .error(ErrorConstants.EXPIRED_TOKEN)
                    .timestamp(LocalDateTime.now())
                    .statusMessage(HttpStatus.UNAUTHORIZED.toString())
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \""+errorResponse.getError()+"\",\"statusMessage\":\""+errorResponse.getStatusMessage()+"\",\"Timestamp:\""+errorResponse.getTimestamp()+"\"\n}");
        } else {
            ErrorResponse errorResponse= ErrorResponse.builder()
                    .error(ErrorConstants.INVALID_TOKEN)
                    .timestamp(LocalDateTime.now())
                    .statusMessage(HttpStatus.UNAUTHORIZED.toString())
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \""+errorResponse.getError()+"\",\"statusMessage\":\""+errorResponse.getStatusMessage()+"\",\"Timestamp:\""+errorResponse.getTimestamp()+"\"\n}");
    }
}
}