package com.georeference.controllers;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.appregca.repositories.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class WelcomeController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/municipality")
    public ResponseEntity<List<Municipality>> getMunicipalities() {
        return ResponseEntity.ok(municipalityRepository.findAll());
    }

    @GetMapping
    public ResponseEntity<String> getWelcome() {
        return ResponseEntity.ok().body("Welcome to Georeference Service");
    }
}
