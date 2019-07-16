package com.tamboot.wechatty.core;

import com.tamboot.wechatty.config.TambootWechattyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import space.chensheng.wechatty.common.message.MessageListener;
import space.chensheng.wechatty.mp.util.MpAppContext;
import space.chensheng.wechatty.mp.util.WechatMpBootstrap;

import java.util.Collection;

public class MpAppContextFactoryBean implements FactoryBean<MpAppContext> {
    private static final Logger logger = LoggerFactory.getLogger(MpAppContextFactoryBean.class);

    private TambootWechattyProperties properties;

    private Collection<MessageListener<?>> msgListeners;

    public MpAppContextFactoryBean(TambootWechattyProperties properties, Collection<MessageListener<?>> msgListeners) {
        Assert.notNull(properties, "properties must not be null");
        Assert.notNull(properties.getAppId(), "[tamboot.wechatty.appId] must not be null");
        Assert.notNull(properties.getAppSecret(), "[tamboot.wechatty.appSecret] must not be null");

        this.properties = properties;
        this.msgListeners = msgListeners;
    }

    @Override
    public MpAppContext getObject() throws Exception {
        WechatMpBootstrap bootstrap = new WechatMpBootstrap();
        customizeWechatContext(bootstrap);
        initMsgListeners(bootstrap);
        initPayCert(bootstrap);
        return bootstrap.build();
    }

    @Override
    public Class<?> getObjectType() {
        return MpAppContext.class;
    }

    private void customizeWechatContext(WechatMpBootstrap bootstrap) {
        bootstrap.customizeWechatContext((wechatContext) -> {
                wechatContext.setToken(properties.getToken());
                wechatContext.setAesKey(properties.getAesKey());
                wechatContext.setAppId(properties.getAppId());
                wechatContext.setAppSecret(properties.getAppSecret());
                wechatContext.setEnableCryptedMode(properties.isEnableCryptedMode());
                wechatContext.setPayCertFile(properties.getPayCertFile());
                wechatContext.setPayClientIp(properties.getPayClientIp());
                wechatContext.setPayKey(properties.getPayKey());
                wechatContext.setPayMchId(properties.getPayMchId());
                wechatContext.setPayNotifyUrl(properties.getPayNotifyUrl());
                wechatContext.setRefundNotifyUrl(properties.getRefundNotifyUrl());
                wechatContext.setAccessTokenStrategyClass(properties.getAccessTokenStrategyClass());
                wechatContext.setAutoUpdateAccessToken(properties.isAutoUpdateAccessToken());
                wechatContext.setPoolingHttpConnectionRequestTimeoutMillis(properties.getPoolingHttpConnectionRequestTimeoutMillis());
                wechatContext.setPoolingHttpConnectTimeoutMillis(properties.getPoolingHttpConnectTimeoutMillis());
                wechatContext.setPoolingHttpMaxPerRoute(properties.getPoolingHttpMaxPerRoute());
                wechatContext.setPoolingHttpMaxTotal(properties.getPoolingHttpMaxTotal());
                wechatContext.setPoolingHttpProxyEnable(properties.isPoolingHttpProxyEnable());
                wechatContext.setPoolingHttpProxyHostname(properties.getPoolingHttpProxyHostname());
                wechatContext.setPoolingHttpProxyPassword(properties.getPoolingHttpProxyPassword());
                wechatContext.setPoolingHttpProxyPort(properties.getPoolingHttpProxyPort());
                wechatContext.setPoolingHttpProxyUsername(properties.getPoolingHttpProxyUsername());
                wechatContext.setPoolingHttpSocketTimeoutMillis(properties.getPoolingHttpSocketTimeoutMillis());
                wechatContext.setPoolingHttpTcpNoDelay(properties.isPoolingHttpTcpNoDelay());
        });
    }

    private void initMsgListeners(WechatMpBootstrap bootstrap) {
        if (CollectionUtils.isEmpty(msgListeners)) {
            logger.info("No message listeners found!");
            return;
        }

        for (MessageListener<?> msgListener : msgListeners) {
            bootstrap.addMsgListener(msgListener);
        }
        logger.info("Register {} message listeners", msgListeners.size());
    }

    private void initPayCert(WechatMpBootstrap bootstrap) {
        if (properties.isEnablePayCert()) {
            bootstrap.enablePayCert();
        }
    }
}
