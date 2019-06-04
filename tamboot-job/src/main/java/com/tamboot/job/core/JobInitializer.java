package com.tamboot.job.core;

import com.tamboot.common.utils.ExceptionUtils;
import com.tamboot.job.config.TambootJobProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.Semaphore;

public class JobInitializer implements ApplicationListener<ApplicationReadyEvent> {
	private final Log logger = LogFactory.getLog(getClass());
	
	private volatile boolean hasStarted = false;
	
	private Semaphore startSemaphore = new Semaphore(1);
	
	public JobInitializer() {
	}
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (hasStarted) {
			return;
		}
		
		try {
			if (!startSemaphore.tryAcquire()) {
				return;
			}
			Scheduler scheduler = getScheduler();
			scheduler.start();
			this.scheduleRefreshMemoJob(scheduler);
			hasStarted = true;
		} catch (SchedulerException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
		} finally {
			startSemaphore.release();
		}
	}

	private void scheduleRefreshMemoJob(Scheduler scheduler) {
		TambootJobProperties properties = getProperties();
		JobDetail refreshJobDetail = JobBuilder
				.newJob(RefreshMemoJob.class)
				.withIdentity(JobConstants.JOB_ID_REFRESH, JobConstants.SYSTEM_JOB_GROUP)
				.build();
		
		Trigger refreshTrigger = TriggerBuilder
				.newTrigger()
				.withIdentity(JobConstants.TRIGGER_ID_REFRESH, JobConstants.SYSTEM_TRIGGER_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(properties.getRefreshCron()))
				.build();
		
		try {
			scheduler.scheduleJob(refreshJobDetail, refreshTrigger);
		} catch (SchedulerException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
		}
	}

	private Scheduler getScheduler() {
		return JobAppContextHolder.get().getBean(JobConstants.SCHEDULER_BEAN_NAME, Scheduler.class);
	}

	private TambootJobProperties getProperties() {
		return JobAppContextHolder.get().getBean(TambootJobProperties.class);
	}
}
