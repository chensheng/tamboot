package com.tamboot.job.core;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;

public class RefreshMemoJob implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataRepository dataRepos = getRepository();
		Collection<JobData> jobDatas = dataRepos.load();
		
		JobMemo jobMemo = this.getMemo();
		jobMemo.refresh(jobDatas);
	}

	private JobDataRepository getRepository() {
		return JobAppContextHolder.get().getBean(JobDataRepositoryFactory.class).get();
	}

	private JobMemo getMemo() {
		return JobAppContextHolder.get().getBean(JobMemo.class);
	}
	
}
