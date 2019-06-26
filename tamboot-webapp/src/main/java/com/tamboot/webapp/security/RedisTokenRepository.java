package com.tamboot.webapp.security;

import com.tamboot.common.tools.text.RedisKeyFactory;
import com.tamboot.security.token.TokenRepository;
import com.tamboot.webapp.core.RedisNamespace;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;

import java.util.concurrent.TimeUnit;

public class RedisTokenRepository implements TokenRepository {
    private RedisTemplate redisTemplate;

    public RedisTokenRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean contains(String token, int expirySeconds) {
        String key = createKey(token);
        Object securityContext = redisTemplate.opsForValue().get(key);
        return securityContext != null;
    }

    @Override
    public SecurityContext load(String token, int expirySeconds) {
        String key = createKey(token);
        Object securityContext = redisTemplate.opsForValue().get(key);
        if (securityContext == null || !SecurityContext.class.isAssignableFrom(securityContext.getClass())) {
            return null;
        }

        return (SecurityContext) securityContext;
    }

    @Override
    public void save(String token, SecurityContext securityContext, int expirySeconds) {
        String key = createKey(token);
        redisTemplate.opsForValue().set(key, securityContext);
        if (expirySeconds > 0) {
            redisTemplate.expire(key, expirySeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void delete(String token) {
        String key = createKey(token);
        redisTemplate.delete(key);
    }

    private String createKey(String token) {
        return RedisKeyFactory.create(RedisNamespace.TOKEN.value(), token);
    }
}
