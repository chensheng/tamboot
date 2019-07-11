package com.tamboot.rocketmq.client.core;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.config.RocketMQConfigUtils;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

public class TambootRocketMQTemplate {
    private RocketMQTemplate delegate;

    public TambootRocketMQTemplate(RocketMQTemplate delegate) {
        Assert.notNull(delegate, "[delegate] must not be null");
        this.delegate = delegate;
    }

    public RocketMQTemplate getDelegate() {
        return delegate;
    }

    /**
     * <p> Send message in synchronous mode. This method returns only when the sending procedure totally completes.
     * Reliable synchronous transmission is used in extensive scenes, such as important notification messages, SMS
     * notification, SMS marketing system, etc.. </p>
     * <p>
     * <strong>Warn:</strong> this method has internal retry-mechanism, that is, internal implementation will retry
     * {@link DefaultMQProducer#getRetryTimesWhenSendFailed} times before claiming failure. As a result, multiple
     * messages may potentially delivered to broker(s). It's up to the application developers to resolve potential
     * duplication issue.
     *
     * @param destination formats: `topicName:tags`
     * @param msg message payload or completed {@link org.springframework.messaging.Message}
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Object msg) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msg, "[msg] must not be null");
        if (Message.class.isAssignableFrom(msg.getClass())) {
            return delegate.syncSend(destination, (Message) msg);
        } else {
            return delegate.syncSend(destination, msg);
        }
    }

    /**
     * Send message in synchronized mode with specified delay.
     * @param destination formats: `topicName:tags`
     * @param msg message payload or completed {@link org.springframework.messaging.Message}
     * @param delay delay time
     * @return send result
     */
    public SendResult syncSendWithDelay(String destination, Object msg, MessageDelayLevel delay) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msg, "[msg] must not be null");
        Assert.notNull(delay, "[delay] must not be null");

        Message message = wrapPayloadInNeed(msg);
        return delegate.syncSend(destination, message, delegate.getProducer().getSendMsgTimeout(), delay.getCode());
    }

    /**
     * Send message orderly in synchronized mode with hashKey by specified.
     *
     * @param destination formats: `topicName:tags`
     * @param msg message payload or completed {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Object msg, String hashKey) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msg, "[msg] must not be null");

        if (Message.class.isAssignableFrom(msg.getClass())) {
            return delegate.syncSendOrderly(destination, (Message) msg, hashKey);
        } else {
            return delegate.syncSendOrderly(destination, msg, hashKey);
        }
    }

    /**
     * Send batch message in synchronized mode
     * @param destination formats: `topicName:tags`
     * @param msgs message payloads or completed {@link org.springframework.messaging.Message}s
     * @return send result
     */
    public SendResult syncSendBatch(String destination, Collection<?> msgs) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msgs, "[msgs] must not be null");

        Collection<Object> convertedMsgs = new ArrayList<Object>();
        for (Object msg : msgs) {
            if (!Message.class.isAssignableFrom(msg.getClass())) {
                convertedMsgs.add(MessageBuilder.withPayload(msg).build());
            } else {
                convertedMsgs.add(msg);
            }
        }

        return delegate.syncSend(destination, (Collection) convertedMsgs, delegate.getProducer().getSendMsgTimeout());
    }

    /**
     * Send message in Transaction using default txProducerGroup.
     * @param destination destination formats: `topicName:tags`
     * @param msg message {@link org.springframework.messaging.Message}
     * @param arg ext arg using in {@link RocketMQLocalTransactionListener#executeLocalTransaction(Message, Object)}
     * @return TransactionSendResult
     * @throws MessagingException
     */
    public TransactionSendResult syncSendInTransaction(String destination, Object msg, Object arg) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msg, "[msg] must not be null");

        Message message = wrapPayloadInNeed(msg);
        return delegate.sendMessageInTransaction(RocketMQConfigUtils.ROCKETMQ_TRANSACTION_DEFAULT_GLOBAL_NAME, destination, message, arg);
    }

    private Message wrapPayloadInNeed(Object msg) {
        Message message = null;
        if (Message.class.isAssignableFrom(msg.getClass())) {
            message = (Message) msg;
        } else {
            message = MessageBuilder.withPayload(msg).build();
        }
        return message;
    }
}
