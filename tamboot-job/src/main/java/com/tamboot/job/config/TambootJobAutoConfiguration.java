package com.tamboot.job.config;

import com.tamboot.job.core.*;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(TambootJobProperties.class)
@AutoConfigureBefore(QuartzAutoConfiguration.class)
public class TambootJobAutoConfiguration {

    @Bean
    public JobAppContextHolder jobAppContextHolder() {
        return new JobAppContextHolder();
    }

    @Bean(JobConstants.SCHEDULER_BEAN_NAME)
    public Scheduler scheduler(TambootJobProperties properties) throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Properties schedulerConf = new Properties();
        schedulerConf.setProperty("org.quartz.threadPool.threadCount", properties.getThreadCount()+"");
        schedulerFactory.initialize(schedulerConf);
        return schedulerFactory.getScheduler();
    }

    @Bean
    public JobMemo jobMemo(@Qualifier(JobConstants.SCHEDULER_BEAN_NAME) Scheduler scheduler) {
        return new JobMemo(scheduler);
    }

    @Bean
    public JobDataRepositoryFactory jobDataRepositoryFactory() {
        return new JobDataRepositoryFactory();
    }

    @Bean
    public JobShutdownHook jobShutdownHook(@Qualifier(JobConstants.SCHEDULER_BEAN_NAME) Scheduler scheduler) {
        return new JobShutdownHook(scheduler);
    }
}
