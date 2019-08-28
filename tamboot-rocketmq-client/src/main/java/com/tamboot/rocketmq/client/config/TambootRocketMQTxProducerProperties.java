package com.tamboot.rocketmq.client.config;

import org.apache.rocketmq.common.MixAll;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rocketmq.tx-producer")
public class TambootRocketMQTxProducerProperties {
    private String group;

    /**
     * Millis of send message timeout.
     */
    private int sendMessageTimeout = 3000;

    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    private int compressMessageBodyThreshold = 1024 * 4;

    /**
     * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimesWhenSendFailed = 2;

    /**
     * <p> Maximum number of retry to perform internally before claiming sending failure in asynchronous mode. </p>
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimesWhenSendAsyncFailed = 2;

    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    private boolean retryNextServer = false;

    /**
     * Maximum allowed message size in bytes.
     */
    private int maxMessageSize = 1024 * 1024 * 4;

    /**
     * The property of "access-key".
     */
    private String accessKey;

    /**
     * The property of "secret-key".
     */
    private String secretKey;

    /**
     * Switch flag instance for message trace.
     */
    private boolean enableMsgTrace = true;

    /**
     * The name value of message trace topic.If you don't config,you can use the default trace topic name.
     */
    private String customizedTraceTopic = MixAll.RMQ_SYS_TRACE_TOPIC;

    int corePoolSize = 1;

    /**
     * Set ExecutorService params -- maximumPoolSize
     */
    int maximumPoolSize = 1;

    /**
     * Set ExecutorService params -- keepAliveTime
     */
    long keepAliveTime = 1000 * 60; //60ms

    /**
     * Set ExecutorService params -- blockingQueueSize
     */
    int blockingQueueSize = 2000;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getSendMessageTimeout() {
        return sendMessageTimeout;
    }

    public void setSendMessageTimeout(int sendMessageTimeout) {
        this.sendMessageTimeout = sendMessageTimeout;
    }

    public int getCompressMessageBodyThreshold() {
        return compressMessageBodyThreshold;
    }

    public void setCompressMessageBodyThreshold(int compressMessageBodyThreshold) {
        this.compressMessageBodyThreshold = compressMessageBodyThreshold;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public int getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    public boolean isRetryNextServer() {
        return retryNextServer;
    }

    public void setRetryNextServer(boolean retryNextServer) {
        this.retryNextServer = retryNextServer;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isEnableMsgTrace() {
        return enableMsgTrace;
    }

    public void setEnableMsgTrace(boolean enableMsgTrace) {
        this.enableMsgTrace = enableMsgTrace;
    }

    public String getCustomizedTraceTopic() {
        return customizedTraceTopic;
    }

    public void setCustomizedTraceTopic(String customizedTraceTopic) {
        this.customizedTraceTopic = customizedTraceTopic;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getBlockingQueueSize() {
        return blockingQueueSize;
    }

    public void setBlockingQueueSize(int blockingQueueSize) {
        this.blockingQueueSize = blockingQueueSize;
    }
}
