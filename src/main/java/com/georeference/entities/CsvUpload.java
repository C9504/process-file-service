package com.georeference.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "csv_uploads")
@Data
public class CsvUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime uploadTimestamp;

    public CsvUpload() {
        this.uploadTimestamp = LocalDateTime.now();
    }
}
