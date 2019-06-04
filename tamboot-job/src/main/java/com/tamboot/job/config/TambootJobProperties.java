package com.tamboot.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tamboot.job")
public class TambootJobProperties {
    private String refreshCron = "0 0/1 * * * ?";

    private int threadCount = 5;

    public String getRefreshCron() {
        return refreshCron;
    }

    public void setRefreshCron(String refreshCron) {
        this.refreshCron = refreshCron;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
