package com.tamboot.sample.xxljob;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@JobHandler("testXxlJob")
public class TestXxlJob extends IJobHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestXxlJob.class);

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("test xxl job is running");
        return ReturnT.SUCCESS;
    }
}
