package com.tamboot.sample.model;

import com.tamboot.webapp.core.BaseModel;

public class SystemJobModel extends BaseModel {
    private static final long serialVersionUID = 6832627477643063402L;

    private String jobBeanName;

    private String triggerCron;

    private String jobParams;

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

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
    }
}
