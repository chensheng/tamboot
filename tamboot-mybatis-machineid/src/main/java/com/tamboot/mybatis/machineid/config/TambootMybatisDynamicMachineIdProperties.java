package com.tamboot.mybatis.machineid.config;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class TambootMybatisDynamicMachineIdProperties {
    private SnowFlake snowFlake;

    public SnowFlake getSnowFlake() {
        return snowFlake;
    }

    public void setSnowFlake(SnowFlake snowFlake) {
        this.snowFlake = snowFlake;
    }

    public static class SnowFlake {
        private DynamicMachineId dynamicMachineId;

        public DynamicMachineId getDynamicMachineId() {
            return dynamicMachineId;
        }

        public void setDynamicMachineId(DynamicMachineId dynamicMachineId) {
            this.dynamicMachineId = dynamicMachineId;
        }
    }

    public static class DynamicMachineId {
        private String database;

        private Duration heartbeat = Duration.ofSeconds(10);

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public Duration getHeartbeat() {
            return heartbeat;
        }

        public void setHeartbeat(Duration heartbeat) {
            this.heartbeat = heartbeat;
        }
    }
}
