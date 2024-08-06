package com.georeference.batch.listeners;

import com.georeference.dto.SicaProcessFileDto;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.repositories.GeoreferenceRecordRepository;
import com.georeference.services.FileService;
import com.georeference.services.sica.producers.SicaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchJobExecuteListener implements JobExecutionListener {

    private final SicaProducer sicaProducer;
    private final GeoreferenceRecordRepository georeferenceRecordRepository;
    private final FileService fileService;

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
        String loadId = jobParameters.getString("loadId");
        try {
            List<GeoreferenceRecord> georeferenceRecords = georeferenceRecordRepository.getByRequestId(requestId);
            System.out.println(georeferenceRecords.isEmpty() ? "Es nulo" : "CORRECTO");
            if (!georeferenceRecords.isEmpty()) {
                String content = fileService.convertCSVToString(filePath);
                Integer regs = georeferenceRecordRepository.getRegs(requestId);
                sicaProducer.sendMessage(
                        SicaProcessFileDto
                                .builder()
                                .id(loadId)
                                .fileName(loadId + ".csv")
                                .regs(regs)
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
