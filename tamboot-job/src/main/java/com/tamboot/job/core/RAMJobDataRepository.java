package com.tamboot.job.core;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class RAMJobDataRepository implements JobDataRepository {
	private ConcurrentHashMap<String, JobData> dataMap = new ConcurrentHashMap<String, JobData>();

	@SuppressWarnings("unchecked")
	@Override
	public Collection<JobData> load() {
		return CollectionUtils.unmodifiableCollection(dataMap.values());
	}

}
