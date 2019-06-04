package com.tamboot.security.core;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LazySecurityContextRepository implements SecurityContextRepository {
    private ApplicationContext applicationContext;

    public LazySecurityContextRepository(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return get().loadContext(requestResponseHolder);
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        get().saveContext(context, request, response);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return get().containsContext(request);
    }

    private SecurityContextRepository get() {
        return applicationContext.getBean(SecurityContextRepository.class);
    }
}
