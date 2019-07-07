package com.tamboot.webapp.security;

import com.tamboot.security.token.TokenRepository;
import org.springframework.security.core.context.SecurityContext;

import java.time.Duration;

public class RedisTokenRepository implements TokenRepository {
    private SecurityRedisTemplate redisTemplate;

    public RedisTokenRepository(SecurityRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean contains(String token, int expirySeconds) {
        Object securityContext = redisTemplate.get(SecurityRedisNamespace.TOKEN, token);
        return securityContext != null;
    }

    @Override
    public SecurityContext load(String token, int expirySeconds) {
        Object securityContext = redisTemplate.get(SecurityRedisNamespace.TOKEN, token);
        if (securityContext == null || !SecurityContext.class.isAssignableFrom(securityContext.getClass())) {
            return null;
        }

        return (SecurityContext) securityContext;
    }

    @Override
    public void save(String token, SecurityContext securityContext, int expirySeconds) {
        if (expirySeconds > 0) {
            redisTemplate.set(SecurityRedisNamespace.TOKEN, token, securityContext, Duration.ofSeconds(expirySeconds));
        } else {
            redisTemplate.set(SecurityRedisNamespace.TOKEN, token, securityContext);
        }
    }

    @Override
    public void delete(String token) {
        redisTemplate.delete(SecurityRedisNamespace.TOKEN, token);
    }

}
