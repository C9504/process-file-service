package com.georeference.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "georeference_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoreferenceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exporterDocumentType;
    private String exporterDocumentNumber;
    private String fileName;
    private Date requestDate;
    private String status; // EN PROCESO, CON ERROR, FINALIZADO
    private String reportName;
    private String zipId;
}
