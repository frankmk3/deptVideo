package com.dept.video.server.security;

import com.dept.video.server.dto.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper;

    @Autowired
    public AuthenticationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String responseMsg = objectMapper.writeValueAsString(Response.builder()
                .status(HttpStatus.UNAUTHORIZED.value()).message("Access denied").build());
        response.getWriter().write(responseMsg);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
