package com.tamboot.job.core;

import java.util.Collection;

public interface JobDataRepository {
	Collection<JobData> load();
}
