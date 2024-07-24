package com.georeference.batch.listeners;

import com.georeference.process.entities.GeoreferenceRecord;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BatchStepExecutionListener implements StepExecutionListener {

    private final FlatFileItemReader<GeoreferenceRecord> reader;
    public BatchStepExecutionListener(FlatFileItemReader<GeoreferenceRecord> reader) {
        this.reader = reader;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Resource resource = new FileSystemResource(Objects.requireNonNull(jobParameters.getString("filePath")));
        reader.setResource(resource);
        StepExecutionListener.super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        System.out.println(jobParameters.getLong("requestId"));
        return ExitStatus.COMPLETED;
    }
}
