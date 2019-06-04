package com.tamboot.rocketmq.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RocketMQInitializer implements ApplicationListener<ApplicationReadyEvent> {
	private Log logger = LogFactory.getLog(getClass());
	
	private AtomicBoolean started = new AtomicBoolean(false);
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (!started.compareAndSet(false, true)) {
			logger.debug("RocketMQ client already started");
			return;
		}
		
		this.startProducers();
		this.startConsumers();
		this.startListenerContainers();
	}

	private void startProducers() {
		Map<String, MQProducer> producers = RocketMQAppContextHolder.get().getBeansOfType(MQProducer.class);
		if (producers == null || producers.size() == 0) {
			return;
		}
		
		for (MQProducer producer : producers.values()) {
			try {
				producer.start();
			} catch (MQClientException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void startConsumers() {
		Map<String, MQConsumer> consumers = RocketMQAppContextHolder.get().getBeansOfType(MQConsumer.class);
		if (consumers == null || consumers.size() == 0) {
			return;
		}
		
		for (MQConsumer consumer : consumers.values()) {
			try {
				if (consumer instanceof MQPushConsumer) {
					((MQPushConsumer) consumer).start();
				} else if (consumer instanceof MQPullConsumer) {
					((MQPullConsumer) consumer).start();
				}
			} catch (MQClientException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void startListenerContainers() {
		Map<String, RocketMQMessageListenerContainer> containers = RocketMQAppContextHolder.get().getBeansOfType(RocketMQMessageListenerContainer.class);
		if (containers == null || containers.size() == 0) {
			return;
		}
		
		for (RocketMQMessageListenerContainer container : containers.values()) {
			try {
				container.start();
			} catch (MQClientException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
