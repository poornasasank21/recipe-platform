package com.user.userservice.exception;

import com.user.userservice.constants.ErrorConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.time.LocalDateTime;

public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
       ErrorResponse errorResponse= ErrorResponse.builder().error(ErrorConstants.ACCESS_DENIED).timestamp(LocalDateTime.now()).statusMessage(HttpStatus.FORBIDDEN.toString()).build();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().print(errorResponse);
        response.getWriter().flush();
    }
}
