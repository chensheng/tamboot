package com.tamboot.security.core;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.security.config.TambootSecurityProperties;
import com.tamboot.security.token.TokenPresenterFactory;
import com.tamboot.security.token.TokenRepositoryFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenSecurityContextRepository implements SecurityContextRepository {
    private TambootSecurityProperties properties;

    private TokenPresenterFactory tokenPresenterFactory;

    private TokenRepositoryFactory tokenRepositoryFactory;

    public TokenSecurityContextRepository(TambootSecurityProperties properties, TokenPresenterFactory tokenPresenterFactory, TokenRepositoryFactory tokenRepositoryFactory) {
        Assert.notNull(properties, "properties must not be null");
        Assert.notNull(tokenPresenterFactory, "tokenPresenterFactory must not be null");
        Assert.notNull(tokenRepositoryFactory, "tokenRepositoryFactory must not be null");
        this.properties = properties;
        this.tokenPresenterFactory = tokenPresenterFactory;
        this.tokenRepositoryFactory = tokenRepositoryFactory;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        String token = tokenPresenterFactory.get(requestResponseHolder.getRequest()).readFromRequest(requestResponseHolder.getRequest());
        if (TextUtil.isEmpty(token)) {
            return this.createNewContext();
        }

        SecurityContext securityContext = tokenRepositoryFactory.get().load(token, properties.getTokenExpirySeconds());
        if (securityContext == null) {
            return this.createNewContext();
        }

        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String token = tokenPresenterFactory.get(request).readFromResponse(request, response);
        if (TextUtil.isEmpty(token)) {
            token = tokenPresenterFactory.get(request).readFromRequest(request);
        }

        if (TextUtil.isEmpty(token)) {
            return;
        }

        tokenRepositoryFactory.get().save(token, context, properties.getTokenExpirySeconds());
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String token = tokenPresenterFactory.get(request).readFromRequest(request);
        if (TextUtil.isEmpty(token)) {
            return false;
        }

        return tokenRepositoryFactory.get().contains(token, properties.getTokenExpirySeconds());
    }

    private SecurityContext createNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }
}
