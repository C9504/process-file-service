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
    @JoinColumn(name = "GEOREFERENCE_REQUEST_ID")
    private GeoreferenceRequest georeferenceRequest;
    @Column(name = "FARMER_NAME", length = 200)
    private String farmerName;
    @Column(name = "DOCUMENT_TYPE", length = 4)
    private String documentType;
    @Column(name = "DOCUMENT_NUMBER", length = 15)
    private String documentNumber;
    @Column(name = "FARM_NAME", length = 200)
    private String farmName;
    @Column(name = "CULTIVATION_AREA", length = 15)
    private Double cultivationArea;
    @Column(name = "MUNICIPALITY_CODE", length = 3)
    private String municipalityCode;
    @Column(name = "MUNICIPALITY_NAME", length = 100)
    private String municipalityName;
    @Column(name = "DEPARTMENT_CODE", length = 3)
    private String departmentCode;
    @Column(name = "DEPARTMENT_NAME", length = 100)
    private String departmentName;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "GEO_JSON_ID")
    private String geoJsonId;
    @Column(name = "OLD_PLOT")
    private Boolean oldPlot;

    // Codigo departamento pertenezca al municipio
    // Codi dep con Name department
    // Municipio con Code Municipio
    // Tipos de documentos concuerden
}
