package com.tamboot.job.config;

import com.tamboot.job.core.JobData;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;

@ConfigurationProperties(prefix = "tamboot.job")
public class TambootJobProperties {
    private String refreshCron = "0 0/1 * * * ?";

    private int threadCount = 5;

    private Collection<JobData> jobs;

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

    public Collection<JobData> getJobs() {
        return jobs;
    }

    public void setJobs(Collection<JobData> jobs) {
        this.jobs = jobs;
    }
}
