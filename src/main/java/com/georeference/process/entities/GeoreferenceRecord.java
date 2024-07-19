package com.georeference.process.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "georeference_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoreferenceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GeoreferenceRequest georeferenceRequest;
    @Column(length = 200)
    private String farmerName;
    @Column(length = 4)
    private String documentType;
    @Column(length = 15)
    private Integer documentNumber;
    @Column(length = 200)
    private String farmName;
    @Column(length = 15)
    private Double cultivationArea;
    @Column(length = 3)
    private String municipalityCode;
    @Column(length = 100)
    private String municipalityName;
    @Column(length = 3)
    private String departmentCode;
    @Column(length = 100)
    private String departmentName;
    private String status;
    private String geoJsonId;
    private Boolean oldPlot;

    // Codigo departamento pertenezca al municipio
    // Codi dep con Name department
    // Municipio con Code Municipio
    // Tipos de documentos concuerden
}
