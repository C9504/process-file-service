package com.georeference.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin
public class WelcomeController {

    @GetMapping
    public ResponseEntity<String> getWelcome() {
        return ResponseEntity.ok().body("Welcome to Georeference Service");
    }
}
