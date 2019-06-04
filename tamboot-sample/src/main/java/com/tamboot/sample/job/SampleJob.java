package com.tamboot.sample.job;

import com.tamboot.job.core.Job;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleJob implements Job {
    private Log logger = LogFactory.getLog(getClass());

    @Override
    public void execute(Map<String, Object> params) {
        logger.info("sample job is executing");
        if (params == null) {
            logger.info("no job params found");
        } else {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                logger.info("job param: name["+entry.getKey()+"] value["+entry.getValue()+"]");
            }
        }
    }
}
