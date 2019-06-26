package com.tamboot.job.core;

import com.tamboot.common.tools.base.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class JobShutdownHook implements ApplicationListener<ContextClosedEvent> {
	private final Log logger = LogFactory.getLog(getClass());
    
    private Scheduler scheduler;
    
    public JobShutdownHook(Scheduler scheduler) {
    	this.scheduler = scheduler;
    }
	
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		try {
			this.scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error(ExceptionUtil.stackTraceText(e));
		}
	}

}
