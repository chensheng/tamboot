package com.tamboot.rocketmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tamboot.rocketmq")
public class TambootRocketMQProperties {
    private String namesrv;

    private Producer simpleProducer;

    private Producer transactionProducer;

    public String getNamesrv() {
        return namesrv;
    }

    public void setNamesrv(String namesrv) {
        this.namesrv = namesrv;
    }

    public Producer getSimpleProducer() {
        return simpleProducer;
    }

    public void setSimpleProducer(Producer simpleProducer) {
        this.simpleProducer = simpleProducer;
    }

    public Producer getTransactionProducer() {
        return transactionProducer;
    }

    public void setTransactionProducer(Producer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    public static class Producer {
        private String group;

        private int sendMsgTimeout = 3000;

        private int compressMsgBodyOverHowMuch = 1024 * 4; //4K

        private int retryTimesWhenSendFailed = 2;

        private int retryTimesWhenSendAsyncFailed = 2;

        private boolean retryAnotherBrokerWhenNotStoreOk = false;

        private int maxMessageSize = 1024 * 1024 * 4; //4M

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getSendMsgTimeout() {
            return sendMsgTimeout;
        }

        public void setSendMsgTimeout(int sendMsgTimeout) {
            this.sendMsgTimeout = sendMsgTimeout;
        }

        public int getCompressMsgBodyOverHowMuch() {
            return compressMsgBodyOverHowMuch;
        }

        public void setCompressMsgBodyOverHowMuch(int compressMsgBodyOverHowMuch) {
            this.compressMsgBodyOverHowMuch = compressMsgBodyOverHowMuch;
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

        public boolean isRetryAnotherBrokerWhenNotStoreOk() {
            return retryAnotherBrokerWhenNotStoreOk;
        }

        public void setRetryAnotherBrokerWhenNotStoreOk(boolean retryAnotherBrokerWhenNotStoreOk) {
            this.retryAnotherBrokerWhenNotStoreOk = retryAnotherBrokerWhenNotStoreOk;
        }

        public int getMaxMessageSize() {
            return maxMessageSize;
        }

        public void setMaxMessageSize(int maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
        }

    }
}
