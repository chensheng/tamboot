package com.tamboot.rocketmq.client.config;

import com.tamboot.rocketmq.client.core.TambootRocketMQTemplate;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class TambootRocketMQClientAutoConfiguration {

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public TambootRocketMQTemplate tambootRocketMQTemplate(RocketMQTemplate rocketMQTemplate) {
        return new TambootRocketMQTemplate(rocketMQTemplate);
    }
}
