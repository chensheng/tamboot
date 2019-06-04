package com.tamboot.webapp.security;

import com.tamboot.webapp.core.JsonResponseWriter;
import com.tamboot.webapp.core.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    private JsonResponseWriter writer;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        writer.write(response, ResponseEntity.ACCESS_DENIED);
    }
}
