package com.tamboot.security.token;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class TokenRepositoryFactory {
    private final TokenRepository defaultTokenRepository = new InMemoryTokenRepository();

    private ApplicationContext applicationContext;

    public TokenRepositoryFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public TokenRepository get() {
        try {
            return applicationContext.getBean(TokenRepository.class);
        } catch (BeansException e) {
            return defaultTokenRepository;
        }
    }
}
