package com.georeference.batch.listeners;

import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

@Component
public class BatchJobExecuteListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);
    }

}
