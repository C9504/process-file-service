package com.georeference.services;

import com.georeference.entities.Landmark;

import java.util.List;
import java.util.Optional;

public interface LandmarkService  {
    void createLandmark(String name, double latitude, double longitude, String address, String description);
    Optional<Landmark> getLandMarkById(Long landmarkId);
    List<Landmark> getAllLandmarks();
}
