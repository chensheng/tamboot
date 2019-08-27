package com.tamboot.rocketmq.client.tx;

import com.tamboot.rocketmq.client.core.MessageHeader;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;

import java.util.Iterator;

public class DefaultLocalTransactionListener implements RocketMQLocalTransactionListener {
    private ObjectProvider<TransactionMessageChecker<?, ?>> checkerProvider;

    public DefaultLocalTransactionListener(ObjectProvider<TransactionMessageChecker<?, ?>> checkerProvider) {
        Assert.notNull(checkerProvider, "checkerProvider must not be null");
        this.checkerProvider = checkerProvider;
    }

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        TransactionMessageChecker<?, ?> checker = findChecker(headers);
        if (checker == null) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        String checkParams = getMessageHeaderQuietly(headers, MessageHeader.CHECK_PARAMS);
        return checker.check(checkParams);
    }

    private TransactionMessageChecker<?, ?> findChecker(MessageHeaders headers) {
        String msgType = getMessageHeaderQuietly(headers, MessageHeader.MSG_TYPE);
        Iterator<TransactionMessageChecker<?,?>> checkerIterator = checkerProvider.iterator();
        while (checkerIterator.hasNext()) {
            TransactionMessageChecker<?, ?> checker = checkerIterator.next();
            if (checker.supports(msgType)) {
                return checker;
            }
        }
        return null;
    }

    private String getMessageHeaderQuietly(MessageHeaders headers, String name) {
        try {
            return headers.get(name, String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
