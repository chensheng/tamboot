package com.tamboot.job.core;

import java.util.Map;

public class JobData {
	private String jobId;

	private String jobBeanName;
	
	private String triggerCron;
	
	private Map<String, Object> params;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobBeanName() {
		return jobBeanName;
	}

	public void setJobBeanName(String jobBeanName) {
		this.jobBeanName = jobBeanName;
	}

	public String getTriggerCron() {
		return triggerCron;
	}

	public void setTriggerCron(String triggerCron) {
		this.triggerCron = triggerCron;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobBeanName == null) ? 0 : jobBeanName.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((triggerCron == null) ? 0 : triggerCron.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobData other = (JobData) obj;
		if (jobBeanName == null) {
			if (other.jobBeanName != null)
				return false;
		} else if (!jobBeanName.equals(other.jobBeanName))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (triggerCron == null) {
			if (other.triggerCron != null)
				return false;
		} else if (!triggerCron.equals(other.triggerCron))
			return false;
		return true;
	}

	
	
}
