package com.tamboot.security.core;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.security.token.TokenPresenterFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LazyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private AuthenticationSuccessHandler defaultHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    private ApplicationContext applicationContext;

    public LazyAuthenticationSuccessHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = getTokenPresenterFactory().get(request).generate(request, authentication);
        if (TextUtil.isNotEmpty(token)) {
            getTokenPresenterFactory().get(request).write(request, response, token);
        }
        getHandler().onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationSuccessHandler getHandler() {
        try {
            return applicationContext.getBean(AuthenticationSuccessHandler.class);
        } catch (BeansException e) {
            return defaultHandler;
        }
    }

    private TokenPresenterFactory getTokenPresenterFactory() {
        return applicationContext.getBean(TokenPresenterFactory.class);
    }
}
