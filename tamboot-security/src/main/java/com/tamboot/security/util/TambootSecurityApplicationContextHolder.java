package com.tamboot.security.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TambootSecurityApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TambootSecurityApplicationContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext get() {
        return applicationContext;
    }
}
