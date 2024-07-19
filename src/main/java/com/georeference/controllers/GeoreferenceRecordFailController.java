package com.georeference.controllers;

import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.services.GeoreferenceRecordFailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/georeference-record-fails")
@Slf4j
@Tag(name = "Georeference Record Fail", description = "API to Georeference Record Fail operations")
public class GeoreferenceRecordFailController {

    private final AtomicLong counter = new AtomicLong(0);

    private final GeoreferenceRecordFailService georeferenceRecordFailService;

    public GeoreferenceRecordFailController(GeoreferenceRecordFailService georeferenceRecordFailService) {
        this.georeferenceRecordFailService = georeferenceRecordFailService;
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "GET Georeference Record Fail by Id", description = "Description")
    public List<GeoreferenceRecordFail> getGeoreferenceRecordFailsById(@PathVariable("requestId") Long requestId) {
        long started = System.currentTimeMillis();
        List<GeoreferenceRecordFail> grrf = georeferenceRecordFailService.getGeoreferenceRecordFails(requestId);
        long invocationNumber = counter.getAndIncrement();
        log.info("GeoreferenceRecordFailController#getGeoreferenceRecordFailsById(): georreferenceRecordFails invocation {} returning successfully | #{} timed out after {} ms", invocationNumber, invocationNumber, System.currentTimeMillis() - started);
        return grrf;
    }
}
