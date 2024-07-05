package com.georeference.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detailed_validation_errors")
@Data
public class DetailedValidationError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uploadId;
    private Long recordId;
    private String fieldName;
    private String errorMessage;

    public DetailedValidationError() {}

    public DetailedValidationError(Long uploadId, Long recordId, String fieldName, String errorMessage) {
        this.uploadId = uploadId;
        this.recordId = recordId;
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }
}
