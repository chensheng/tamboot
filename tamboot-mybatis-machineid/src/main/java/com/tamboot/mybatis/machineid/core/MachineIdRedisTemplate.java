package com.tamboot.mybatis.machineid.core;

import com.tamboot.redis.core.TambootRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;

public class MachineIdRedisTemplate extends TambootRedisTemplate<MachineIdRedisNamespace> {
    public MachineIdRedisTemplate(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String resolveNamespaceValue(MachineIdRedisNamespace namespace) {
        return "tamboot:mybatis:machineId:" + namespace.getCode();
    }
}
