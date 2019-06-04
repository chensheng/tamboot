package com.tamboot.sample.job;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamboot.common.utils.ExceptionUtils;
import com.tamboot.job.core.JobData;
import com.tamboot.job.core.JobDataRepository;
import com.tamboot.sample.mapper.SystemJobMapper;
import com.tamboot.sample.model.SystemJobModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseJobDataRepository implements JobDataRepository {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SystemJobMapper systemJobMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Collection<JobData> load() {
        List<SystemJobModel> jobModels = systemJobMapper.selectAll();
        if (CollectionUtils.isEmpty(jobModels)) {
            return null;
        }

        List<JobData> jobDataList = new ArrayList<JobData>();
        for (SystemJobModel jobModel : jobModels) {
            JobData jobData = new JobData();
            jobData.setJobId(jobModel.getId() + "");
            jobData.setJobBeanName(jobModel.getJobBeanName());
            jobData.setTriggerCron(jobModel.getTriggerCron());
            jobData.setParams(parseJobParams(jobModel.getJobParams()));
            jobDataList.add(jobData);
        }

        return jobDataList;
    }

    private Map<String, Object> parseJobParams(String jobParams) {
        if (StringUtils.isEmpty(jobParams)) {
            return null;
        }

        try {
            return objectMapper.readValue(jobParams, Map.class);
        } catch (JsonParseException e) {
            logger.error(ExceptionUtils.getStackTraceAsString(e));
        } catch (JsonMappingException e) {
            logger.error(ExceptionUtils.getStackTraceAsString(e));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTraceAsString(e));
        }
        return null;
    }
}
