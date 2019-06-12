package com.tamboot.job.core;

import com.tamboot.job.config.TambootJobProperties;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

public class InMemoryJobDataRepository implements JobDataRepository {
	@SuppressWarnings("unchecked")
	@Override
	public Collection<JobData> load() {
	    try {
            TambootJobProperties props = JobAppContextHolder.get().getBean(TambootJobProperties.class);
            Collection<JobData> jobs = props.getJobs();
            if (!CollectionUtils.isEmpty(jobs)) {
                return Collections.unmodifiableCollection(jobs);
            }
        } catch (BeansException e) {
        }
        return null;
	}

}
