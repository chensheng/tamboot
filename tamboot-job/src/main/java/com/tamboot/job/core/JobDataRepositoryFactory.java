package com.tamboot.job.core;

import org.springframework.beans.BeansException;

public class JobDataRepositoryFactory {
	private static final JobDataRepository DEFAULT_REPO = new InMemoryJobDataRepository();

	public JobDataRepository get() {
		try {
			return JobAppContextHolder.get().getBean(JobDataRepository.class);
		} catch (BeansException e) {
			return DEFAULT_REPO;
		}
	}

}
