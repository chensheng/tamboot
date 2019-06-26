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

@RocketMQConsumer(consumerGroup = "topicTransactionGroup", topic = "TopicTransaction")
public class TopicTransactionListener implements MessageListenerConcurrently {
	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		for (MessageExt msg : msgs) {
			try {
				String content = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
				logger.info("Receive msg rom TopicTransaction, content[" + content + "]");
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtil.stackTraceText(e));
			}
		}
		
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
