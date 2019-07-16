package com.tamboot.wechatty.config;

import com.tamboot.wechatty.core.MpAppContextFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.chensheng.wechatty.common.message.MessageListener;

import java.util.List;

@Configuration
@EnableConfigurationProperties(TambootWechattyProperties.class)
public class TambootWechattyAutoConfiguration {
    
    @Autowired(required = false)
    private List<MessageListener<?>> msgListeners;

    @Bean
    public MpAppContextFactoryBean mpAppContext(TambootWechattyProperties properties) {
        return new MpAppContextFactoryBean(properties, msgListeners);
    }
}
