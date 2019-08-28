package com.tamboot.rocketmq.client.event;

import com.tamboot.common.tools.mapper.JsonMapper;
import com.tamboot.common.tools.reflect.ReflectionUtil;
import com.tamboot.common.tools.text.TextUtil;

public abstract class EventHandler<T extends Event> {
    private Class<?> eventType;

    private T emptyEvent;

    public EventHandler() {
        eventType = ReflectionUtil.findGenericType(this, EventHandler.class, "T");
        try {
            emptyEvent = (T) eventType.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("[" + eventType + "] should have a non-argument constructor");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("[" + eventType + "] should have a non-argument constructor");
        }
    }

    protected boolean supports(EventMessage message) {
        if (message == null || TextUtil.isEmpty(message.getType())) {
            return false;
        }
        return message.getType().equals(emptyEvent.type());
    }

    protected T resolveEvent(EventMessage message) {
        if (message == null || TextUtil.isEmpty(message.getData())) {
            return null;
        }

        return (T) JsonMapper.nonNullMapper().fromJson(message.getData(), eventType);
    }

    boolean handle(EventMessage message) {
        if (!supports(message)) {
            return false;
        }

        T event = resolveEvent(message);
        doHandle(event);
        return true;
    }

    protected abstract void doHandle(T event);
}
