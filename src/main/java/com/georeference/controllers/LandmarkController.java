package com.georeference.controllers;

import com.georeference.entities.Landmark;
import com.georeference.services.LandmarkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/landmarks")
public class LandmarkController {

    private final LandmarkService landmarkService;

    public LandmarkController(LandmarkService landmarkService) {
        this.landmarkService = landmarkService;
    }

    @GetMapping
    public List<Landmark> getLandmarks() {
        return landmarkService.getAllLandmarks();
    }
}
