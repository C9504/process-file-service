package com.georeference.batch.listeners;

import com.georeference.dto.SicaProcessFileDto;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.process.repositories.GeoreferenceRecordRepository;
import com.georeference.services.FileService;
import com.georeference.services.GeoreferenceRequestService;
import com.georeference.services.sica.producers.SicaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BatchJobExecuteListener implements JobExecutionListener {

    private final GeoreferenceRequestService georeferenceRequestService;
    private final SicaProducer sicaProducer;
    private final GeoreferenceRecordRepository georeferenceRecordRepository;
    private final FileService fileService;

    public BatchJobExecuteListener(
            GeoreferenceRequestService georeferenceRequestService,
            FileService fileService,
            SicaProducer sicaProducer,
            GeoreferenceRecordRepository georeferenceRecordRepository
    ) {
        this.georeferenceRequestService = georeferenceRequestService;
        this.fileService = fileService;
        this.sicaProducer = sicaProducer;
        this.georeferenceRecordRepository = georeferenceRecordRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobParameters jp = jobExecution.getJobParameters();
        String fileName = jp.getString("fileName");
        log.info("Nombre del archivo: {}", fileName);
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        Long requestId = jobParameters.getLong("requestId");
        String filePath = jobParameters.getString("filePath");
        String fileName = jobParameters.getString("fileName");
        try {
            String content = fileService.convertCSVToString(filePath);
            GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId);
            List<GeoreferenceRecord> georeferenceRecords = georeferenceRecordRepository.getByRequestId(requestId);
            if (!georeferenceRecords.isEmpty()) {
                sicaProducer.sendMessage(
                        SicaProcessFileDto
                                .builder()
                                .id(Objects.requireNonNull(georeferenceRequest).getLoadId())
                                .fileName(fileName)
                                .content(content)
                                .build());
                log.info("Sending file to SICA: {}", fileName);
            }
        } catch (IOException e) {
            log.error("BtachJobExecuteListener: {}", e.getMessage());
        }
        JobExecutionListener.super.afterJob(jobExecution);
    }

}
