package com.user.userservice.exception;

import com.user.userservice.constants.ErrorConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiAccessDeniedHandlerTest {
    @InjectMocks
    private ApiAccessDeniedHandler apiAccessDeniedHandler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void whenAccessDenied_thenSetForbiddenStatusAndReturnErrorJson() throws IOException {
        AccessDeniedException accessDeniedException = new AccessDeniedException(ErrorConstants.ACCESS_DENIED);
        apiAccessDeniedHandler.handle(request, response, accessDeniedException);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        printWriter.flush(); // Important to flush the writer to make content available for the test
        String responseContent = responseWriter.toString();
        assertEquals("statusMessage=403 FORBIDDEN, error=Access is denied to current resource)", responseContent.substring(responseContent.indexOf("statusMessage")));
    }
}