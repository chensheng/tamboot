package com.tamboot.rocketmq.config;

import com.tamboot.rocketmq.core.RocketMQAppContextHolder;
import com.tamboot.rocketmq.core.RocketMQConsumerRegistry;
import com.tamboot.rocketmq.core.SimpleMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TambootRocketMQProperties.class)
public class TambootRocketMQAutoConfiguration {
    @Bean
    public RocketMQAppContextHolder rocketMQAppContextHolder() {
        return new RocketMQAppContextHolder();
    }

    @Bean
    public RocketMQConsumerRegistry rocketMQConsumerRegistry() {
        return new RocketMQConsumerRegistry();
    }

    @Bean
    @ConditionalOnProperty(name = {"tamboot.rocketmq.namesrv", "tamboot.rocketmq.simpleProducer.group"})
    public SimpleMQProducer simpleMQProducer(TambootRocketMQProperties rocketMQProps) {
        SimpleMQProducer simpleProducer = new SimpleMQProducer(rocketMQProps.getSimpleProducer().getGroup());
        simpleProducer.setNamesrvAddr(rocketMQProps.getNamesrv());
        simpleProducer.setSendMsgTimeout(rocketMQProps.getSimpleProducer().getSendMsgTimeout());
        simpleProducer.setRetryTimesWhenSendFailed(rocketMQProps.getSimpleProducer().getRetryTimesWhenSendFailed());
        simpleProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProps.getSimpleProducer().getRetryTimesWhenSendAsyncFailed());
        simpleProducer.setMaxMessageSize(rocketMQProps.getSimpleProducer().getMaxMessageSize());
        return simpleProducer;
    }

    @Bean
    @ConditionalOnProperty(name = {"tamboot.rocketmq.namesrv", "tamboot.rocketmq.transactionProducer.group"})
    public TransactionMQProducer transactionMQProducer(TambootRocketMQProperties rocketMQProps) {
        TransactionMQProducer transactionProducer = new TransactionMQProducer(rocketMQProps.getTransactionProducer().getGroup());
        transactionProducer.setNamesrvAddr(rocketMQProps.getNamesrv());
        transactionProducer.setSendMsgTimeout(rocketMQProps.getTransactionProducer().getSendMsgTimeout());
        transactionProducer.setRetryTimesWhenSendFailed(rocketMQProps.getTransactionProducer().getRetryTimesWhenSendFailed());
        transactionProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProps.getTransactionProducer().getRetryTimesWhenSendAsyncFailed());
        transactionProducer.setMaxMessageSize(rocketMQProps.getTransactionProducer().getMaxMessageSize());
        transactionProducer.setTransactionCheckListener((msg) -> LocalTransactionState.COMMIT_MESSAGE);
        return transactionProducer;
    }
}
