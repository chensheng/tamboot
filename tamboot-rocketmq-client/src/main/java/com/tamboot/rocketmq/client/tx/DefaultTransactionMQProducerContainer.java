package com.tamboot.rocketmq.client.tx;

import com.tamboot.rocketmq.client.config.TambootRocketMQTxProducerProperties;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultTransactionMQProducerContainer {
    private TambootRocketMQTxProducerProperties properties;

    private ApplicationContext applicationContext;

    private RocketMQTemplate rocketMQTemplate;

    private ObjectProvider<TransactionMessageChecker<?, ?>> checkers;

    public DefaultTransactionMQProducerContainer(TambootRocketMQTxProducerProperties properties, RocketMQTemplate rocketMQTemplate, ObjectProvider<TransactionMessageChecker<?, ?>> checkers, ApplicationContext applicationContext) {
        Assert.notNull(properties, "properties must not be null");
        Assert.notNull(rocketMQTemplate, "rocketMQTemplate must not be null");
        Assert.notNull(checkers, "checkers must not be null");
        Assert.notNull(applicationContext, "applicationContext must not be null");
        this.properties = properties;
        this.rocketMQTemplate = rocketMQTemplate;
        this.checkers = checkers;
        this.applicationContext = applicationContext;
        createAndStartTxProducer();
    }

    public void createAndStartTxProducer() {
        RPCHook rpcHook = RocketMQUtil.getRPCHookByAkSk(applicationContext.getEnvironment(), properties.getAccessKey(), properties.getSecretKey());
        ThreadPoolExecutor checkExecutor = new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(), properties.getKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(properties.getBlockingQueueSize()));
        RocketMQLocalTransactionListener txListener = new DefaultLocalTransactionListener(checkers);
        rocketMQTemplate.createAndStartTransactionMQProducer(properties.getGroup(), txListener, checkExecutor, rpcHook);
    }
}
