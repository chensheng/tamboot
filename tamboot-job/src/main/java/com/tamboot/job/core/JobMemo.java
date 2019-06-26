package com.tamboot.job.core;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.common.tools.text.TextUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

import java.util.*;

public class JobMemo {
	private final Log logger = LogFactory.getLog(getClass());
	
	private HashMap<String, JobData> jobMemo = new HashMap<String, JobData>();

	private Scheduler scheduler;

	public JobMemo(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	public synchronized void refresh(Collection<JobData> jobDatas) {
		Map<String, JobData> latestJobMap = createLatestJobMap(jobDatas);
		List<String> removedJobIds = new ArrayList<String>();
		List<String> updatedJobIds = new ArrayList<String>();
		List<String> addedJobIds = new ArrayList<String>();
		
		for (String jobId : jobMemo.keySet()) {
			if (!latestJobMap.containsKey(jobId)) {
				removedJobIds.add(jobId);
				continue;
			}
			
			JobData oldData = jobMemo.get(jobId);
			JobData latestData = latestJobMap.get(jobId);
			if (!oldData.equals(latestData)) {
				updatedJobIds.add(jobId);
			}
		}
		
		for (String jobId : latestJobMap.keySet()) {
			if (!jobMemo.containsKey(jobId)) {
				addedJobIds.add(jobId);
			}
		}
		
		this.doRemoveJob(removedJobIds);
		this.doUpdateJob(updatedJobIds, latestJobMap);
		this.doAddJob(addedJobIds, latestJobMap);
	}
	
	private void doRemoveJob(List<String> jobIds) {
		for (String jobId : jobIds) {
			jobMemo.remove(jobId);
			try {
				scheduler.unscheduleJob(TriggerKey.triggerKey(jobId));
			} catch (SchedulerException e) {
				logger.error(ExceptionUtil.stackTraceText(e));
			}
		}
	}
	
	private void doUpdateJob(List<String> jobIds, Map<String, JobData> latestJobMap) {
		for (String jobId : jobIds) {
			JobData jobData = latestJobMap.get(jobId);
			jobMemo.put(jobId, jobData);
			TriggerKey triggerKey = TriggerKey.triggerKey(jobId);
			Trigger newTrigger = this.createTrigger(jobData);
			try {
				scheduler.rescheduleJob(triggerKey, newTrigger);
			} catch (SchedulerException e) {
				logger.error(ExceptionUtil.stackTraceText(e));
			}
		}
	}
	
	private void doAddJob(List<String> jobIds, Map<String, JobData> latestJobMap) {
		for (String jobId : jobIds) {
			JobData jobData = latestJobMap.get(jobId);
			jobMemo.put(jobId, jobData);
			Trigger newTrigger = this.createTrigger(jobData);
			try {
				scheduler.scheduleJob(createDelegatingJobDetail(jobId), newTrigger);
			} catch (SchedulerException e) {
				logger.error(ExceptionUtil.stackTraceText(e));
			}
		}
	}
	
	private JobDetail createDelegatingJobDetail(String jobId) {
		return JobBuilder
				.newJob(DelegatingJob.class)
				.withIdentity(JobConstants.JOB_ID_DELEGATING + "-" + jobId, JobConstants.SYSTEM_JOB_GROUP)
				.build();
	}
	
	private Trigger createTrigger(JobData jobData) {
		TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
				.withIdentity(TriggerKey.triggerKey(jobData.getJobId()))
				.withSchedule(CronScheduleBuilder.cronSchedule(jobData.getTriggerCron()));
		
		JobDataMap params = new JobDataMap();
		if (jobData.getParams() != null) {
			params.putAll(jobData.getParams());
		}
		params.put(JobConstants.PARAM_JOB_BEAN_NAME, jobData.getJobBeanName());
		builder.usingJobData(params);
		
		return builder.build();
	}
	
	private Map<String, JobData> createLatestJobMap(Collection<JobData> jobDatas) {
		Map<String, JobData> latestJobMap = new HashMap<String, JobData>();
		
		if (CollectionUtils.isEmpty(jobDatas)) {
			return latestJobMap;
		}
		
		for (JobData jobData : jobDatas) {
			if (TextUtil.isEmpty(jobData.getJobId())) {
				continue;
			}
			
			latestJobMap.put(jobData.getJobId(), jobData);
		}
		
		return latestJobMap;
	}
}
