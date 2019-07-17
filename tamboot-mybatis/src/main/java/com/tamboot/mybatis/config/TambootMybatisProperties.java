package com.tamboot.mybatis.config;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class TambootMybatisProperties {
    private boolean ignoreInterceptor;

    private boolean throwVersionLockException;

    private SnowFlake snowFlake = new SnowFlake();

    public boolean getIgnoreInterceptor() {
        return ignoreInterceptor;
    }

    public void setIgnoreInterceptor(boolean ignoreInterceptor) {
        this.ignoreInterceptor = ignoreInterceptor;
    }

    public boolean getThrowVersionLockException() {
        return throwVersionLockException;
    }

    public void setThrowVersionLockException(boolean throwVersionLockException) {
        this.throwVersionLockException = throwVersionLockException;
    }

    public SnowFlake getSnowFlake() {
        return snowFlake;
    }

    public void setSnowFlake(SnowFlake snowFlake) {
        this.snowFlake = snowFlake;
    }

    public static class SnowFlake {
        private Long machineId;

        private long twepoch = 1493737860828L;

        public Long getMachineId() {
            return machineId;
        }

        public void setMachineId(Long machineId) {
            this.machineId = machineId;
        }

        public long getTwepoch() {
            return twepoch;
        }

        public void setTwepoch(long twepoch) {
            this.twepoch = twepoch;
        }
    }
}
