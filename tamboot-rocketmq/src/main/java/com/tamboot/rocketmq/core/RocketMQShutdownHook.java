package com.tamboot.rocketmq.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RocketMQShutdownHook implements ApplicationListener<ContextClosedEvent> {
	private Log logger = LogFactory.getLog(getClass());

	private AtomicBoolean showdown = new AtomicBoolean(false);
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		if (!showdown.compareAndSet(false, true)) {
			logger.debug("RocketMQ client already shutdown");
			return;
		}
		
		this.shutdownProducers();
		this.shutdownConsumers();
		this.shutdownListenerContainers();
	}

	private void shutdownProducers() {
		Map<String, MQProducer> producers = RocketMQAppContextHolder.get().getBeansOfType(MQProducer.class);
		if (producers == null || producers.size() == 0) {
			return;
		}
		
		for (MQProducer producer : producers.values()) {
			producer.shutdown();	
		}
	}
	
	private void shutdownConsumers() {
		Map<String, MQConsumer> consumers = RocketMQAppContextHolder.get().getBeansOfType(MQConsumer.class);
		if (consumers == null || consumers.size() == 0) {
			return;
		}
		
		for (MQConsumer consumer : consumers.values()) {
			if (consumer instanceof MQPushConsumer) {
				((MQPushConsumer) consumer).shutdown();
			} else if (consumer instanceof MQPullConsumer) {
				((MQPullConsumer) consumer).shutdown();
			}
		}
	}
	
	private void shutdownListenerContainers() {
		Map<String, RocketMQMessageListenerContainer> containers = RocketMQAppContextHolder.get().getBeansOfType(RocketMQMessageListenerContainer.class);
		if (containers == null || containers.size() == 0) {
			return;
		}
		
		for (RocketMQMessageListenerContainer container : containers.values()) {
			container.shutdown();
		}
	}
}
