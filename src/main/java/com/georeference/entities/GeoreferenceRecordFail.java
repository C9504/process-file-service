package com.georeference.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "georeference_record_fails")
@Data
@NoArgsConstructor
public class GeoreferenceRecordFail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GeoreferenceRequest georeferenceRequest;
    private String columnName;
    private Integer rowNumber;
    private String errorMessage;

    public GeoreferenceRecordFail(GeoreferenceRequest georeferenceRequest, String columnName, Integer rowNumber, String errorMessage) {}
}
