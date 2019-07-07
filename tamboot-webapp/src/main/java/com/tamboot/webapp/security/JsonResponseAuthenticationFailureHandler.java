package com.tamboot.webapp.security;

import com.tamboot.webapp.web.JsonResponseWriter;
import com.tamboot.webapp.web.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private JsonResponseWriter writer;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        writer.write(response, ResponseEntity.USERNAME_PASSWORD_ERROR);
    }
}
