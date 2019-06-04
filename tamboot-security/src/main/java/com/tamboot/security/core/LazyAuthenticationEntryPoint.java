package com.tamboot.security.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LazyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint defaultAuthEntryPoint = new AuthenticationEntryPoint() {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendRedirect("/");
        }
    };

    private ApplicationContext applicationContext;

    public LazyAuthenticationEntryPoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        get().commence(request, response, authException);
    }

    private AuthenticationEntryPoint get() {
        try {
            return applicationContext.getBean(AuthenticationEntryPoint.class);
        } catch (BeansException e) {
            return defaultAuthEntryPoint;
        }
    }
}
