package com.georeference.controllers;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.appregca.repositories.MunicipalityRepository;
import com.georeference.dto.SicaProcessFileDto;
import com.georeference.services.sica.producers.SicaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private SicaProducer sicaProducer;

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/municipality")
    public ResponseEntity<List<Municipality>> getMunicipalities() {
        return ResponseEntity.ok(municipalityRepository.findAll());
    }

    @GetMapping("/send-message")
    public ResponseEntity<String> sendMessage() {
        sicaProducer.sendMessage(SicaProcessFileDto
                .builder()
                .id("0001")
                .fileName("filename.csv")
                .content("content")
                .build());
        return ResponseEntity.ok("ENVIADO");
    }

    @GetMapping
    public ResponseEntity<String> getWelcome() {
        return ResponseEntity.ok().body("Welcome to Georeference Service");
    }
}
