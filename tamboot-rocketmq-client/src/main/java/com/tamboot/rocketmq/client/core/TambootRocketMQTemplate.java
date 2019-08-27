package com.tamboot.rocketmq.client.core;

import com.tamboot.common.tools.mapper.JsonMapper;
import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.rocketmq.client.config.TambootRocketMQEventProperties;
import com.tamboot.rocketmq.client.config.TambootRocketMQTxProducerProperties;
import com.tamboot.rocketmq.client.event.Event;
import com.tamboot.rocketmq.client.event.EventMessage;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;

public class TambootRocketMQTemplate {
    private RocketMQTemplate delegate;

    private TambootRocketMQEventProperties eventProperties;

    private TambootRocketMQTxProducerProperties txProducerProperties;

    public TambootRocketMQTemplate(RocketMQTemplate delegate, TambootRocketMQEventProperties eventProperties, TambootRocketMQTxProducerProperties txProducerProperties) {
        Assert.notNull(delegate, "[delegate] must not be null");
        this.delegate = delegate;
        this.eventProperties = eventProperties;
        this.txProducerProperties = txProducerProperties;
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
     * @param checkParams check parameters using in {@link com.tamboot.rocketmq.client.tx.TransactionMessageChecker#doCheck(Object)}
     * @return transaction send result
     * @throws MessagingException
     */
    public TransactionSendResult syncSendInTransaction(String destination, Object msg, Object checkParams) {
        Assert.notNull(destination, "[destination] must not be null");
        Assert.notNull(msg, "[msg] must not be null");

        String msgType = null;
        MessageBuilder msgBuilder = null;
        if (Message.class.isAssignableFrom(msg.getClass())) {
            Message originalMsg = (Message) msg;
            msgBuilder = MessageBuilder.fromMessage(originalMsg);
            msgType = originalMsg.getPayload().getClass().getName();
        } else {
            msgBuilder = MessageBuilder.withPayload(msg);
            msgType = msg.getClass().getName();
        }
        msgBuilder.setHeader(MessageHeader.MSG_TYPE, msgType);
        msgBuilder.setHeader(MessageHeader.CHECK_PARAMS, encodeCheckParams(checkParams));
        Message message = msgBuilder.build();

        return delegate.sendMessageInTransaction(txProducerProperties.getGroup(), destination, message, null);
    }

    /**
     * Send local event in synchronous mode
     * @param event not null
     * @return send result
     */
    public SendResult syncSendLocalEvent(Event event) {
        return this.syncSendEvent(eventProperties.getTopic() + ":" + eventProperties.getTag(), event);
    }

    /**
     * Send event in synchronous mod
     * @param destination not null
     * @param event not null
     * @return send results
     */
    public SendResult syncSendEvent(String destination, Event event) {
        return this.syncSend(destination, new EventMessage(event));
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

    private String encodeCheckParams(Object checkParams) {
        if (checkParams == null) {
            return TextUtil.EMPTY_STRING;
        }

        if (ClassUtils.isPrimitiveOrWrapper(checkParams.getClass()) || String.class == checkParams.getClass()) {
            return String.valueOf(checkParams);
        }

        return JsonMapper.nonNullMapper().toJson(checkParams);
    }
}
