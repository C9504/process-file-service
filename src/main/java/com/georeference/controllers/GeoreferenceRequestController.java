package com.georeference.controllers;

import com.georeference.entities.GeoreferenceRequest;
import com.georeference.services.FileService;
import com.georeference.services.GeoreferenceRequestService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/georeference-request")
@CrossOrigin
public class GeoreferenceRequestController {

    @Value("${csv.file.path}")
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
    public ResponseEntity<GeoreferenceRequest> getGeoreferenceRequest(@PathVariable("requestId") Long requestId) {
        GeoreferenceRequest gr = georeferenceRequestService.getGeoreferenceRequestById(requestId).get();
        return ResponseEntity.ok(gr);
    }

    @GetMapping("/{exporterDocumentNumber}/me")
    public ResponseEntity<List<GeoreferenceRequest>> getGeoreferenceRequests(@PathVariable("exporterDocumentNumber") String exporterDocumentNumber) {
        List<GeoreferenceRequest> grs = georeferenceRequestService.getGeoreferenceRequestsByDocumentNumber(exporterDocumentNumber);
        return ResponseEntity.ok(grs);
    }

    @PostMapping
    public ResponseEntity<String> insertGeoreferenceRequest(@RequestParam("file") MultipartFile file) {
        GeoreferenceRequest georeferenceRequest = new GeoreferenceRequest();
        georeferenceRequest.setExporterDocumentType("CE");
        georeferenceRequest.setExporterDocumentNumber("1104567890");
        georeferenceRequest.setReportName("report_" + UUID.randomUUID());
        georeferenceRequest.setFileName("fileName_" + UUID.randomUUID());
        georeferenceRequest.setZipId("zip_" + UUID.randomUUID());
        georeferenceRequest.setRequestDate(Date.from(Instant.now()));
        georeferenceRequest.setStatus("PENDING");
        GeoreferenceRequest newReoreferenceRequest = georeferenceRequestService.saveGeoreferenceRequest(georeferenceRequest);
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestId", newReoreferenceRequest.getId())
                .toJobParameters();
        return getStringResponseEntity(file, csvImporterJob, parameters);
    }

    private ResponseEntity<String> getStringResponseEntity(@RequestParam("file") MultipartFile georeferenceRequest, Job csvImporterJob, JobParameters jobParameters) {
        //String striing = fileService.convertXLSXToCSVAndWrite(georeferenceRequest, csvFilePath);
        //System.out.println(striing);
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
