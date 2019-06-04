package com.tamboot.rocketmq.core;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;

public interface RocketMQConsumerLifecycleListener {
	void prepareConsumer(DefaultMQPushConsumer consumer) throws MQClientException;
}
