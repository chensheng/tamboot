package com.tamboot.sample.rocketmq;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.rocketmq.annotation.RocketMQConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RocketMQConsumer(consumerGroup = "topic1Group", topic = "Topic1")
public class Topic1Listener implements MessageListenerConcurrently {
	private Log logger = LogFactory.getLog(getClass());
	
	private AtomicInteger consumeTimes = new AtomicInteger(0);

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		if (consumeTimes.incrementAndGet() % 2 == 0) {
			logger.info("Reconsume later");
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		
		for (MessageExt msg : msgs) {
			try {
				String body = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
				logger.info("Receive msg from Topic1: msg[" + msg + "] body[" + body + "]");
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtil.stackTraceText(e));
			}
		}
		
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
