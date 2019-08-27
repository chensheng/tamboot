package com.tamboot.rocketmq.client.event;

import com.tamboot.common.tools.mapper.JsonMapper;
import org.springframework.util.Assert;

public class EventMessage {
    private String type;

    private String data;

    public  EventMessage() {
    }

    public EventMessage(Event event) {
        Assert.notNull(event, "event must not be null");
        Assert.notNull(event.type(), "event.type() must not be null");
        this.type = event.type();
        this.data = JsonMapper.nonNullMapper().toJson(event);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
