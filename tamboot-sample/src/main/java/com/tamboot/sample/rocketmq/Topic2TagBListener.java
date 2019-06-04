package com.tamboot.sample.rocketmq;

import com.tamboot.rocketmq.annotation.RocketMQConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

@RocketMQConsumer(consumerGroup="topic2TagBGroup", topic="Topic2", selectorExpression="TagB")
public class Topic2TagBListener implements MessageListenerConcurrently {
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		for (MessageExt msg : msgs) {
			logger.info("Receive msg from Topic2 TagB, msg[" + msg + "]");
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
