package com.tamboot.sample.rocketmq;

import com.tamboot.common.utils.ExceptionUtils;
import com.tamboot.rocketmq.annotation.RocketMQConsumer;
import com.tamboot.rocketmq.core.RocketMQConsumerLifecycleListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RocketMQConsumer(consumerGroup="topicOrderlyGroup", topic="TopicOrderly", consumerThreadMin=2, consumerThreadMax=2)
public class TopicOrderlyListener implements MessageListenerOrderly, RocketMQConsumerLifecycleListener {
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
		context.setAutoCommit(true);
		for (MessageExt msg : msgs) {
			try {
				String content = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
				logger.info("Receive msg from TopicOrderly, content[" + content + "]");
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtils.getStackTraceAsString(e));
			}
		}
		
		return ConsumeOrderlyStatus.SUCCESS;
	}

	@Override
	public void prepareConsumer(DefaultMQPushConsumer consumer) {
		logger.info("Begin to prepare consumer");
	}

}
