package com.tamboot.http.config;

import com.tamboot.http.core.EnableTambootHttp;
import com.tamboot.http.httpclient.ApacheHcFeignClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("tamboot.http.basePackage")
@EnableConfigurationProperties(TambootHttpProperties.class)
@EnableTambootHttp
public class TambootHttpAutoConfiguration {
    public static final String DEFAULT_FEIGN_CLIENT_BEAN_NAME = "defaultTambootFeignClient";

    @Configuration
    @ConditionalOnMissingBean(name = {DEFAULT_FEIGN_CLIENT_BEAN_NAME})
    public static class ApacheHttpClientConfig {

        @Bean(DEFAULT_FEIGN_CLIENT_BEAN_NAME)
        public ApacheHcFeignClient apacheHcFeignClient(TambootHttpProperties properties) {
            return new ApacheHcFeignClient(properties);
        }

    }
}
