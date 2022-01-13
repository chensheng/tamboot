package com.tamboot.webapp.config;

import com.tamboot.mybatis.config.TambootMybatisAutoConfiguration;
import com.tamboot.mybatis.id.SnowFlakeIdGeneratorFactory;
import com.tamboot.mybatis.strategy.InsertStrategy;
import com.tamboot.mybatis.strategy.UpdateStrategy;
import com.tamboot.security.permission.RoleBasedPermissionRepository;
import com.tamboot.security.token.TokenRepository;
import com.tamboot.webapp.mybatis.CreateInfoInsertStrategy;
import com.tamboot.webapp.mybatis.ModifyInfoUpdateStrategy;
import com.tamboot.webapp.security.*;
import com.tamboot.webapp.web.JsonResponseWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public JsonResponseWriter jsonResponseWriter(MappingJackson2HttpMessageConverter converter) {
        return new JsonResponseWriter(converter);
    }

    @Configuration
    @ConditionalOnProperty("spring.security.useRedisRepo")
    public static class SecurityRedisRepoConfiguration {
        @Bean
        public SecurityRedisTemplate securityRedisTemplate(RedisTemplate redisTemplate) {
            return new SecurityRedisTemplate(redisTemplate);
        }

        @Bean
        public RoleBasedPermissionRepository roleBasedPermissionRepository(SecurityRedisTemplate redisTemplate) {
            return new RedisRoleBasedPermissionRepository(redisTemplate);
        }

        @Bean
        public TokenRepository redisRepository(SecurityRedisTemplate redisTemplate) {
            return new RedisTokenRepository(redisTemplate);
        }
    }

    @Configuration
    @ConditionalOnClass(TambootMybatisAutoConfiguration.class)
    public static class TambootWebappMybatisConfiguration {
        @Bean
        @ConditionalOnMissingBean(InsertStrategy.class)
        public InsertStrategy insertStrategy(SnowFlakeIdGeneratorFactory idFactory) {
            return new CreateInfoInsertStrategy(idFactory);
        }

        @Bean
        @ConditionalOnMissingBean(UpdateStrategy.class)
        public UpdateStrategy updateStrategy() {
            return new ModifyInfoUpdateStrategy();
        }
    }
}
