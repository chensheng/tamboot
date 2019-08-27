package com.tamboot.rocketmq.client.event;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.rocketmq.client.config.TambootRocketMQEventProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import java.util.Iterator;

@RocketMQMessageListener(topic = "${rocketmq.event.topic}", consumerGroup = "${rocketmq.event.consumerGroup}")
public class EventMessageListener implements RocketMQListener<EventMessage>, RocketMQPushConsumerLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(EventMessageListener.class);

    private TambootRocketMQEventProperties properties;

    private ObjectProvider<EventHandler<?>> handlerProvider;


    public EventMessageListener(TambootRocketMQEventProperties properties, ObjectProvider<EventHandler<?>> handlerProvider) {
        Assert.notNull(properties, "properties must not be null");
        this.properties = properties;
        this.handlerProvider = handlerProvider;
    }

    @Override
    public void onMessage(EventMessage message) {
        Iterator<EventHandler<?>> handlerIt = handlerProvider.iterator();
        while (handlerIt.hasNext()) {
            EventHandler<?> handler = handlerIt.next();
            if (handler.handle(message)) {
                return;
            }
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try {
            consumer.subscribe(properties.getTopic(), properties.getTag());
            consumer.setConsumeThreadMin(properties.getConsumerThreadMin());
            consumer.setConsumeThreadMax(properties.getConsumerThreadMax());
        } catch (MQClientException e) {
            logger.error(ExceptionUtil.stackTraceText(e));
        }
    }
}
