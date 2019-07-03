package com.tamboot.mybatis.machineid.core;

import com.tamboot.mybatis.id.MachineIdStrategy;
import com.tamboot.mybatis.machineid.config.TambootMybatisDynamicMachineIdProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DynamicMachineIdStrategy extends MachineIdStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DynamicMachineIdStrategy.class);

    private MachineIdRedisTemplate machineIdRedisTemplate;

    private TambootMybatisDynamicMachineIdProperties properties;

    private ScheduledExecutorService heartbeatExecutor;

    private Runnable heartbeatTask;

    private Duration heartbeatDuration;

    private Duration retainDuration;

    private String machineIdKey;

    public DynamicMachineIdStrategy(MachineIdRedisTemplate machineIdRedisTemplate, TambootMybatisDynamicMachineIdProperties properties) {
        Assert.notNull(machineIdRedisTemplate, "machineIdRedisTemplate must not be null");
        Assert.notNull(properties, "properties must not be null");
        Assert.notNull(properties.getSnowFlake(), "mybatis.snowFlake must not be null");
        Assert.notNull(properties.getSnowFlake().getDynamicMachineId(), "mybatis.snowFlake.dynamicMachineId must not be null");
        Assert.notNull(properties.getSnowFlake().getDynamicMachineId().getDatabase(), "mybatis.snowFlake.dynamicMachineId.database must not be null");
        Assert.notNull(properties.getSnowFlake().getDynamicMachineId().getHeartbeat(), "mybatis.snowFlake.dynamicMachineId.heartbeat must not be null");

        this.machineIdRedisTemplate = machineIdRedisTemplate;
        this.properties = properties;
        this.heartbeatDuration = properties.getSnowFlake().getDynamicMachineId().getHeartbeat();
        this.retainDuration = heartbeatDuration.multipliedBy(2);
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        this.heartbeatTask = new HeartbeatTask();
    }

    @Override
    protected long generateMachineId() {
        for (long i=MIN_MACHINE_ID; i<=MAX_MACHINE_ID; i++) {
            machineIdKey = createMachineIdKey(i);
            boolean generateSuccess = machineIdRedisTemplate.setIfAbsent(MachineIdRedisNamespace.DYNAMIC, machineIdKey, i, retainDuration);
            if (generateSuccess) {
                startHeartbeat();
                logger.info("Success to generate dynamic machine id: {}", i);
                return i;
            }
        }
        throw new IllegalStateException("Fail to generate machine id");
    }

    public void destroy() {
        if (heartbeatExecutor != null) {
            heartbeatExecutor.shutdownNow();
            heartbeatExecutor = null;
            machineIdRedisTemplate.delete(MachineIdRedisNamespace.DYNAMIC, machineIdKey);
        }
    }

    private String createMachineIdKey(long machineId) {
        return properties.getSnowFlake().getDynamicMachineId().getDatabase() + ":" + machineId;
    }

    private void startHeartbeat() {
        long heartbeatSeconds = heartbeatDuration.getSeconds();
        heartbeatExecutor.scheduleAtFixedRate(heartbeatTask, heartbeatSeconds, heartbeatSeconds, TimeUnit.SECONDS);
    }

    class HeartbeatTask implements Runnable {

        @Override
        public void run() {
            logger.debug("Run dynamic machine id heartbeat task: machineIdKey[{}]", machineIdKey);
            machineIdRedisTemplate.set(MachineIdRedisNamespace.DYNAMIC, machineIdKey, getMachineId(), retainDuration);
        }
    }
}
