package com.tamboot.mybatis.config;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class TambootMybatisProperties {
    private boolean ignoreInterceptor;

    private boolean throwVersionLockException;

    private SnowFlake snowFlake;

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
        private Long dataCenterId;

        private long generatorStartTime;

        public Long getDataCenterId() {
            return dataCenterId;
        }

        public void setDataCenterId(Long dataCenterId) {
            this.dataCenterId = dataCenterId;
        }

        public long getGeneratorStartTime() {
            return generatorStartTime;
        }

        public void setGeneratorStartTime(long generatorStartTime) {
            this.generatorStartTime = generatorStartTime;
        }
    }
}
