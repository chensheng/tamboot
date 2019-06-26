package com.tamboot.job.core;

import com.tamboot.common.tools.base.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;

public class DelegatingJob implements org.quartz.Job {
	private final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
		Job targetJob = this.findTargetJob(jobDataMap);
		if (targetJob == null) {
			return;
		}
		
		try {
			targetJob.execute(jobDataMap.getWrappedMap());
		} catch (Throwable e) {
			logger.error(ExceptionUtil.stackTraceText(e));
		}
	}

	private Job findTargetJob(JobDataMap jobDataMap) {
		String jobBeanName = null;
		try {
			jobBeanName = jobDataMap.getString(JobConstants.PARAM_JOB_BEAN_NAME);
			Object targetJob = JobAppContextHolder.get().getBean(jobBeanName);
			if (Job.class.isAssignableFrom(targetJob.getClass())) {
				return (Job) targetJob;
			}
		} catch (ClassCastException e) {
			logger.error(ExceptionUtil.stackTraceText(e));
		} catch (BeansException e) {
			logger.error("Could not find job bean with name " + jobBeanName);
		}
		return null;
	}
}
