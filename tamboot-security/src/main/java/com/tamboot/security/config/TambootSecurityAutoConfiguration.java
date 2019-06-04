package com.tamboot.security.config;

import com.tamboot.security.core.PasswordEncoderFactory;
import com.tamboot.security.core.TambootAuthenticationProvider;
import com.tamboot.security.core.TambootAuthenticationService;
import com.tamboot.security.core.TokenSecurityContextRepository;
import com.tamboot.security.token.TokenPresenterFactory;
import com.tamboot.security.token.TokenRepositoryFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
@EnableConfigurationProperties(TambootSecurityProperties.class)
@AutoConfigureBefore(SecurityAutoConfiguration.class)
public class TambootSecurityAutoConfiguration {
    @Autowired
    private TambootSecurityProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public TambootWebSecurityConfigurer tambootWebSecurityConfigurer(ObjectProvider<List<AuthenticationProvider>> authProviders) {
        return new TambootWebSecurityConfigurer(properties, authProviders);
    }

    @Bean
    public AuthenticationManager authenticationManager(TambootWebSecurityConfigurer configurer) throws Exception {
        return configurer.authenticationManagerBean();
    }

    @Bean
    public TambootAuthenticationProvider tambootAuthenticationProvider(PasswordEncoderFactory passwordEncoderFactory) {
        return new TambootAuthenticationProvider(applicationContext, passwordEncoderFactory);
    }

    @Bean
    public TokenPresenterFactory tokenPresenterFactory() {
        return new TokenPresenterFactory(properties, applicationContext);
    }

    @Bean
    public TokenRepositoryFactory tokenRepositoryFactory() {
        return new TokenRepositoryFactory(applicationContext);
    }

    @Bean
    public TokenSecurityContextRepository tokenSecurityContextRepository(TokenPresenterFactory tokenPresenterFactory, TokenRepositoryFactory tokenRepositoryFactory) {
        return new TokenSecurityContextRepository(properties, tokenPresenterFactory, tokenRepositoryFactory);
    }

    @Bean
    public PasswordEncoderFactory passwordEncoderFactory(ObjectProvider<PasswordEncoder> passwordEncoders) {
        return new PasswordEncoderFactory(passwordEncoders);
    }

    @Bean
    public TambootAuthenticationService tambootAuthenticationService(PasswordEncoderFactory passwordEncoderFactory, TokenPresenterFactory tokenPresenterFactory, TokenRepositoryFactory tokenRepositoryFactory) {
        return new TambootAuthenticationService(applicationContext, properties, passwordEncoderFactory, tokenPresenterFactory, tokenRepositoryFactory);
    }
}
