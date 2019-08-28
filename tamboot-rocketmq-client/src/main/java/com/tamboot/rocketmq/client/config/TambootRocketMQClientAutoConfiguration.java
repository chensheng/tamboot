package com.tamboot.rocketmq.client.config;

import com.tamboot.rocketmq.client.core.TambootRocketMQTemplate;
import com.tamboot.rocketmq.client.event.EventHandler;
import com.tamboot.rocketmq.client.event.EventMessageListener;
import com.tamboot.rocketmq.client.tx.DefaultTransactionMQProducerContainer;
import com.tamboot.rocketmq.client.tx.TransactionMessageChecker;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
@EnableConfigurationProperties({TambootRocketMQTxProducerProperties.class, TambootRocketMQEventProperties.class})
public class TambootRocketMQClientAutoConfiguration {

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public TambootRocketMQTemplate tambootRocketMQTemplate(RocketMQTemplate rocketMQTemplate, TambootRocketMQEventProperties properties, TambootRocketMQTxProducerProperties txProducerProperties) {
        return new TambootRocketMQTemplate(rocketMQTemplate, properties, txProducerProperties);
    }

    @Bean
    @ConditionalOnProperty("rocketmq.tx-producer.group")
    @ConditionalOnBean(RocketMQTemplate.class)
    public DefaultTransactionMQProducerContainer defaultTransactionMQProducerContainer(TambootRocketMQTxProducerProperties properties, RocketMQTemplate rocketMQTemplate, ObjectProvider<TransactionMessageChecker<?, ?>> checkers, ApplicationContext applicationContext) {
        return new DefaultTransactionMQProducerContainer(properties, rocketMQTemplate, checkers, applicationContext);
    }

    @Bean
    @ConditionalOnProperty(name = {"rocketmq.event.topic", "rocketmq.event.consumerGroup"})
    public EventMessageListener eventMessageListener(TambootRocketMQEventProperties properties, ObjectProvider<EventHandler<?>> handlerProvider) {
        return new EventMessageListener(properties, handlerProvider);
    }
}
