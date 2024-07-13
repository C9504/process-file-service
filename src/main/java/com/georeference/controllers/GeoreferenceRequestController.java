package com.georeference.controllers;

import com.georeference.dto.FileDto;
import com.georeference.entities.GeoreferenceRequest;
import com.georeference.services.FileService;
import com.georeference.services.GeoreferenceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/georeference-request")
@CrossOrigin
@Slf4j
public class GeoreferenceRequestController {

    private final AtomicLong counter = new AtomicLong(0);

    @Value("${csv.output.file.path}")
    private String csvFilePath;

    @Autowired
    private FileService fileService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job csvImporterJob;

    @Autowired
    private GeoreferenceRequestService georeferenceRequestService;

    @GetMapping("/{requestId}")
    public ResponseEntity<GeoreferenceRequest> getGeoreferenceRequestById(@PathVariable("requestId") Long requestId) {
        long started = System.currentTimeMillis();
        GeoreferenceRequest gr = georeferenceRequestService.getGeoreferenceRequestById(requestId).get();
        long invocationNumber = counter.getAndIncrement();
        log.info("GeoreferenceRequestController#getGeoreferenceRequestById(): georreferenceRequest invocation {} returning successfully | #{} timed out after {} ms", invocationNumber, invocationNumber, System.currentTimeMillis() - started);
        return ResponseEntity.ok(gr);
    }

    @GetMapping("/{exporterDocumentNumber}/me")
    public ResponseEntity<List<GeoreferenceRequest>> getGeoreferenceRequestByDocumentNumber(@PathVariable("exporterDocumentNumber") String exporterDocumentNumber) {
        long started = System.currentTimeMillis();
        List<GeoreferenceRequest> grs = georeferenceRequestService.getGeoreferenceRequestsByDocumentNumber(exporterDocumentNumber);
        long invocationNumber = counter.getAndIncrement();
        log.info("GeoreferenceRequestController#getGeoreferenceRequestByDocumentNumber(): georreferenceRequests invocation {} returning successfully | #{} timed out after {} ms", invocationNumber, invocationNumber, System.currentTimeMillis() - started);
        return ResponseEntity.ok(grs);
    }

    @PostMapping
    public ResponseEntity<String> uploadFileBase64(@RequestBody FileDto fileDto) {
        GeoreferenceRequest georeferenceRequest = new GeoreferenceRequest();
        georeferenceRequest.setExporterDocumentType("CE");
        georeferenceRequest.setExporterDocumentNumber(fileDto.getSubject());
        georeferenceRequest.setReportName("report_" + UUID.randomUUID());
        georeferenceRequest.setFileName("georreferencia_" + Instant.now().toEpochMilli() + ".xlsx");
        georeferenceRequest.setZipId("zip_" + UUID.randomUUID());
        georeferenceRequest.setRequestDate(Date.from(Instant.now()));
        georeferenceRequest.setStatus("EN PROCESO");
        GeoreferenceRequest newGeorreferenceRequest = georeferenceRequestService.saveGeoreferenceRequest(georeferenceRequest);
        // Decodificar base64 a bytes
        byte[] decodedBytes = fileService.decodeBase64FileFromString(fileDto.getFileBody());
        String fileName = csvFilePath + newGeorreferenceRequest.getFileName().replace(".xlsx", ".csv");
        try {
            fileService.convertXLSXToCSVAndWrite(decodedBytes, fileName);
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("requestId", newGeorreferenceRequest.getId())
                    .addString("fileName", fileName)
                    .toJobParameters();
            return getStringResponseEntity(csvImporterJob, parameters);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al detectar el tipo de archivo: " + e.getMessage());
        }
    }

    private ResponseEntity<String> getStringResponseEntity(Job csvImporterJob, JobParameters jobParameters) {
        return runJob(csvImporterJob, jobParameters);
    }

    private ResponseEntity<String> runJob(Job job, JobParameters parameters) {
        try {
            JobExecution jobExecution = jobLauncher.run(job, parameters);
            return ResponseEntity.ok("Batch job " + job.getName() + " started with JobExecutionId: " + jobExecution.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start batch job " + job.getName() + " : " + e.getMessage());
        }
    }
}
