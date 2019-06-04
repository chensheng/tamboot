package com.tamboot.job.core;

import java.util.Map;

public interface Job {
	void execute(Map<String, Object> params);
}
