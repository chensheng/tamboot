package com.tamboot.webapp.security;

import com.tamboot.webapp.web.JsonResponseWriter;
import com.tamboot.webapp.web.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private JsonResponseWriter writer;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        writer.write(response, ResponseEntity.NO_AUTHENTICATION);
    }
}
