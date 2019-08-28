package com.tamboot.rocketmq.client.tx;

import com.tamboot.common.tools.mapper.JsonMapper;
import com.tamboot.common.tools.reflect.ReflectionUtil;
import com.tamboot.common.tools.text.TextUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

public abstract class TransactionMessageChecker<MSG, PARAMS> {
    private Class<?> msgType;

    private Class<?> paramsType;

    public TransactionMessageChecker() {
        this.msgType = ReflectionUtil.findGenericType(this, TransactionMessageChecker.class, "MSG");
        this.paramsType = ReflectionUtil.findGenericType(this, TransactionMessageChecker.class, "PARAMS");
    }

    boolean supports(String msgType) {
        return this.msgType.getName().equals(msgType);
    }

    RocketMQLocalTransactionState check(String checkParams) {
        Object params = null;
        if (ClassUtils.isPrimitiveOrWrapper(paramsType)) {
            params = TextUtil.parseToPrimitive(checkParams, paramsType);
        } else {
            params = JsonMapper.nonNullMapper().fromJson(checkParams, paramsType);
        }

        if (params == null) {
            return doCheck(null);
        } else {
            return doCheck((PARAMS) params);
        }
    }

    protected abstract RocketMQLocalTransactionState doCheck(PARAMS params);
}
