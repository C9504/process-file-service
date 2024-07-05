package com.georeference.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "landmarks")
@Data
public class Landmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private String description;
    private Long uploadId; // Relación con CsvUpload

    // Constructor, getters, and setters
    public Landmark() {}

    public Landmark(String name, double latitude, double longitude, String address, String description, Long uploadId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
        this.uploadId = uploadId;
    }
}
