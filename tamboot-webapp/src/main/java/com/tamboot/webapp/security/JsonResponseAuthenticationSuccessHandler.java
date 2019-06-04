package com.tamboot.webapp.security;

import com.tamboot.webapp.core.JsonResponseWriter;
import com.tamboot.webapp.core.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JsonResponseWriter writer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        writer.write(response, ResponseEntity.LOGIN_SUCCESS);
    }
}
