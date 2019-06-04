package com.tamboot.rocketmq.core;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class SimpleMQProducer extends DefaultMQProducer {
	public SimpleMQProducer() {
		super();
	}
	
	public SimpleMQProducer(final String producerGroup) {
		super(producerGroup);
	}
}
