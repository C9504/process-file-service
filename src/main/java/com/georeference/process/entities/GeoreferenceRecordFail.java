package com.georeference.process.entities;

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
    @Column(name ="ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "GEOREFERENCE_REQUEST_ID")
    private GeoreferenceRequest georeferenceRequest;
    @Column(name = "COLUMN_NAME")
    private String columnName;
    @Column(name = "ROW_NUMBER")
    private Integer rowNumber;
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    public GeoreferenceRecordFail(GeoreferenceRequest georeferenceRequest, String columnName, Integer rowNumber, String errorMessage) {}
}
