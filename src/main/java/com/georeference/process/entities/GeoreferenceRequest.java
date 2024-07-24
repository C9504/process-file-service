package com.georeference.process.entities;

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
    @Column(name = "LOAD_ID", insertable = false, updatable = false, unique = true)
    private String loadId;
    @Column(name = "EXPORTER_DOCUMENT_TYPE")
    private String exporterDocumentType;
    @Column(name = "EXPORTER_DOCUMENT_NUMBER")
    private String exporterDocumentNumber;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "REQUEST_DATE")
    private Date requestDate;
    @Column(name = "STATUS")
    private String status; // EN PROCESO, CON ERROR, FINALIZADO
    @Column(name = "REPORT_NAME")
    private String reportName;
    @Column(name = "ZIP_ID")
    private String zipId;

}
