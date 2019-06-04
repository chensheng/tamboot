package com.tamboot.sample.controller;

import com.tamboot.rocketmq.core.SimpleMQProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/common/rocketmq")
public class RocketMQController {
	private Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private SimpleMQProducer simpleProducer;
	
	@Autowired
	private TransactionMQProducer transactionProducer;
	
	private AtomicInteger sendTransactionTimes = new AtomicInteger(0);
	
	@GetMapping("/sendSync")
	public SendResult sendSync(String topic, String tag, String content) throws Exception {
		if (topic == null) {
			topic = "Topic1";
		}
		if (tag == null) {
			tag = "TagA";
		}
		if (content == null) {
			content = "Hello World! 你好世界!";
		}
		Message msg = new Message(topic, tag, content.getBytes(RemotingHelper.DEFAULT_CHARSET));
		return simpleProducer.send(msg);
	}
	
	@GetMapping("/sendSyncOrderly")
	public SendResult sendSyncOrderly(String topic, String tag) throws Exception {
		if (topic == null) {
			topic = "TopicOrderly";
		}
		if (tag == null) {
			tag = "TagA";
		}
		
		SendResult result = null;
		for (int i = 0; i < 5; i++) {
			Message msg = new Message(topic, tag, (i + "").getBytes(RemotingHelper.DEFAULT_CHARSET));
			result = simpleProducer.send(msg, new MessageQueueSelector() {

				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					String parameter = (String) arg;
					logger.info("Parameter is " + parameter);
					return mqs.get(0);
				}
				
			}, "parameter" + i);
		}
		
		return result;
	}
	
	@GetMapping("/sendSyncTransaction")
	public SendResult sendSyncTransaction(String topic, String tag) throws Exception {
		if (topic == null) {
			topic = "TopicTransaction";
		}
		if (tag == null) {
			tag = "TagA";
		}
		
		sendTransactionTimes.incrementAndGet();
		Message msg = new Message(topic, tag, ("Transaction msg" + sendTransactionTimes.get()).getBytes(RemotingHelper.DEFAULT_CHARSET));
		return transactionProducer.sendMessageInTransaction(msg, new LocalTransactionExecuter() {

			@Override
			public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
				Integer times = (Integer) arg;
				logger.info("Send transaction times is " + times);
				if (times % 2 == 0) {
					return LocalTransactionState.ROLLBACK_MESSAGE;
				}
				
				return LocalTransactionState.COMMIT_MESSAGE;
			}
			
		}, sendTransactionTimes.get());
	}
}
