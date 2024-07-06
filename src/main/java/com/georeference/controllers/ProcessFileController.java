package com.georeference.controllers;

import com.georeference.dto.FileDto;
import com.georeference.services.FileService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/process-file")
@CrossOrigin
public class ProcessFileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importLandmarksJob;

    @PostMapping("/validation")
    public ResponseEntity<String> startValidation(@RequestParam("file") MultipartFile file) {
        String outputPath = "C:\\Users\\Administrador\\Programming\\converted.csv";

        try {
            fileService.convertXLSXToCSVAndWrite(file, outputPath);
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(importLandmarksJob, parameters);
            return ResponseEntity.ok("Job started");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo: " + e.getMessage());
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
