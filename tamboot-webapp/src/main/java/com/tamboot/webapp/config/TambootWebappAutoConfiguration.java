package com.tamboot.webapp.config;

import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.strategy.InsertStrategy;
import com.tamboot.mybatis.strategy.UpdateStrategy;
import com.tamboot.security.permission.RoleBasedPermissionRepository;
import com.tamboot.security.token.TokenRepository;
import com.tamboot.web.config.ResponseBodyDecorator;
import com.tamboot.webapp.core.JsonResponseWriter;
import com.tamboot.webapp.core.PageResponseBodyDecorator;
import com.tamboot.webapp.core.SecurityRedisTemplate;
import com.tamboot.webapp.mybatis.CreateInfoInsertStrategy;
import com.tamboot.webapp.mybatis.ModifyInfoUpdateStrategy;
import com.tamboot.webapp.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class TambootWebappAutoConfiguration {
    @Bean
    @ConditionalOnProperty("spring.security.useRedisRepo")
    public SecurityRedisTemplate securityRedisTemplate(RedisTemplate redisTemplate) {
        return new SecurityRedisTemplate(redisTemplate);
    }

    @Bean
    @ConditionalOnProperty("spring.security.useRedisRepo")
    public RoleBasedPermissionRepository roleBasedPermissionRepository(SecurityRedisTemplate redisTemplate) {
        return new RedisRoleBasedPermissionRepository(redisTemplate);
    }

    @Bean
    @ConditionalOnProperty("spring.security.useRedisRepo")
    public TokenRepository redisRepository(SecurityRedisTemplate redisTemplate) {
        return new RedisTokenRepository(redisTemplate);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JsonResponseAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JsonResponseAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new JsonResponseAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new JsonResponseAuthenticationFailureHandler();
    }

    @Bean
    public InsertStrategy insertStrategy(SnowFlakeIdGeneratorFactory idFactory) {
        return new CreateInfoInsertStrategy(idFactory);
    }

    @Bean
    public UpdateStrategy updateStrategy() {
        return new ModifyInfoUpdateStrategy();
    }

    @Bean
    public JsonResponseWriter jsonResponseWriter(MappingJackson2HttpMessageConverter converter) {
        return new JsonResponseWriter(converter);
    }

    @Bean
    public ResponseBodyDecorator responseBodyDecorator() {
        return new PageResponseBodyDecorator();
    }
}
