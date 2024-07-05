package com.georeference.impl;

import com.georeference.entities.Landmark;
import com.georeference.repositories.LandmarkRepository;
import com.georeference.services.LandmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LandmarkServiceImpl implements LandmarkService {

    /**
     * private String name;
     *     private double latitude;
     *     private double longitude;
     *     private String address;
     *     private String description;
     */

    @Autowired
    LandmarkRepository landmarkRepository;

    @Override
    public void createLandmark(String name, double latitude, double longitude, String address, String description) {
        Landmark landmark = new Landmark();
        landmark.setName(name);
        landmark.setLatitude(latitude);
        landmark.setLongitude(longitude);
        landmark.setAddress(address);
        landmark.setDescription(description);
        landmarkRepository.save(landmark);
    }

    @Override
    public Optional<Landmark> getLandMarkById(Long landmarkId) {
        return landmarkRepository.findById(landmarkId);
    }

    @Override
    public List<Landmark> getAllLandmarks() {
        return landmarkRepository.findAll();
    }
}
