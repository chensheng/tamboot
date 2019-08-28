package com.tamboot.rocketmq.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rocketmq.event")
public class TambootRocketMQEventProperties {
    private String topic;

    private String tag = "*";

    private String consumerGroup;

    private int consumerThreadMin = 1;

    private int consumerThreadMax = 1;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public int getConsumerThreadMin() {
        return consumerThreadMin;
    }

    public void setConsumerThreadMin(int consumerThreadMin) {
        this.consumerThreadMin = consumerThreadMin;
    }

    public int getConsumerThreadMax() {
        return consumerThreadMax;
    }

    public void setConsumerThreadMax(int consumerThreadMax) {
        this.consumerThreadMax = consumerThreadMax;
    }
}
