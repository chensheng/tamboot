package com.tamboot.security.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LazyAccessDeniedHandler implements AccessDeniedHandler {
    private AccessDeniedHandler defaultHandler = new AccessDeniedHandlerImpl();

    private ApplicationContext applicationContext;

    public LazyAccessDeniedHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        get().handle(request, response, accessDeniedException);
    }

    private AccessDeniedHandler get() {
        try {
            return applicationContext.getBean(AccessDeniedHandler.class);
        } catch (BeansException e) {
            return defaultHandler;
        }
    }
}
