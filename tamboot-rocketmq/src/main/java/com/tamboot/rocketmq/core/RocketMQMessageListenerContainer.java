package com.tamboot.rocketmq.core;

import com.tamboot.common.utils.StringUtils;
import com.tamboot.rocketmq.annotation.RocketMQConsumer;
import com.tamboot.rocketmq.config.TambootRocketMQProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class RocketMQMessageListenerContainer implements InitializingBean {
	private TambootRocketMQProperties rocketMQProps;
	
	private MessageListener listener;
	
	private RocketMQConsumer config;
	
	private DefaultMQPushConsumer consumer;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(rocketMQProps, "Property [rocketMQProps] must not be null");
		Assert.notNull(listener, "Property [listener] must not be null");
		Assert.notNull(config, "Property [config] must not be null");
		
		this.initConsumer();
	}
	
	@SuppressWarnings("deprecation")
	private void initConsumer() throws MQClientException {
		consumer = new DefaultMQPushConsumer();
		consumer.setNamesrvAddr(rocketMQProps.getNamesrv());
		consumer.setConsumeThreadMin(config.consumerThreadMin());
		consumer.setConsumeThreadMax(config.consumerThreadMax());
		
		if (StringUtils.isNotEmpty(config.consumerGroup())) {
			consumer.setConsumerGroup(config.consumerGroup());
		}
		
		if (StringUtils.isNotEmpty(config.topic())) {
			if (config.selectorType() == SelectorType.TAG) {
				consumer.subscribe(config.topic(), config.selectorExpression());
			} else if (config.selectorType() == SelectorType.SQL92) {
				consumer.subscribe(config.topic(), MessageSelector.bySql(config.selectorExpression()));
			}
		}
		
		if (MessageListenerConcurrently.class.isAssignableFrom(listener.getClass())) {
			consumer.registerMessageListener((MessageListenerConcurrently) listener);
		} else if (MessageListenerOrderly.class.isAssignableFrom(listener.getClass())) {
			consumer.registerMessageListener((MessageListenerOrderly) listener);
		} else {
			consumer.registerMessageListener(listener);
		}
		
		if (listener instanceof RocketMQConsumerLifecycleListener) {
			((RocketMQConsumerLifecycleListener) listener).prepareConsumer(consumer);
		}
	}
	
	void start() throws MQClientException {
		consumer.start();
	}
	
	void shutdown() {
		consumer.shutdown();
	}

	public TambootRocketMQProperties getRocketMQProps() {
		return rocketMQProps;
	}

	public void setRocketMQProps(TambootRocketMQProperties rocketMQProps) {
		this.rocketMQProps = rocketMQProps;
	}

	public MessageListener getListener() {
		return listener;
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}

	public RocketMQConsumer getConfig() {
		return config;
	}

	public void setConfig(RocketMQConsumer config) {
		this.config = config;
	}
	
}
