package com.tamboot.security.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LazyAuthenticationFailureHandler implements AuthenticationFailureHandler{
    private AuthenticationFailureHandler defaultHandler = new SimpleUrlAuthenticationFailureHandler();

    private ApplicationContext applicationContext;

    public LazyAuthenticationFailureHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        getHandler().onAuthenticationFailure(request, response, exception);
    }

    private AuthenticationFailureHandler getHandler() {
        try {
            return applicationContext.getBean(AuthenticationFailureHandler.class);
        } catch (BeansException e) {
            return defaultHandler;
        }
    }

}
