package com.tamboot.security.test.custom;

import com.tamboot.security.token.TokenRepository;
import org.springframework.security.core.context.SecurityContext;

import java.util.concurrent.ConcurrentHashMap;

public class CustomTokenRepository implements TokenRepository {
    private ConcurrentHashMap<String, SecurityContext> contextMap = new ConcurrentHashMap<String, SecurityContext>();

    @Override
    public boolean contains(String token, int expirySeconds) {
        return contextMap.contains(token);
    }

    @Override
    public SecurityContext load(String token, int expirySeconds) {
        return contextMap.get(token);
    }

    @Override
    public void save(String token, SecurityContext securityContext, int expirySeconds) {
        contextMap.put(token, securityContext);
    }

    @Override
    public void delete(String token) {
        contextMap.remove(token);
    }
}
