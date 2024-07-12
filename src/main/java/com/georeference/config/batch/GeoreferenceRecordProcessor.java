package com.georeference.config.batch;

import com.georeference.entities.GeoreferenceRecord;
import com.georeference.entities.GeoreferenceRecordFail;
import com.georeference.entities.GeoreferenceRequest;
import com.georeference.repositories.GeoreferenceValidationErrorsRepository;
import com.georeference.services.GeoreferenceRequestService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;

@JobScope
@Component
public class GeoreferenceRecordProcessor implements ItemProcessor<GeoreferenceRecord, GeoreferenceRecord> {

    private final Long requestId;
    private int row = 1;

    @Autowired
    GeoreferenceRequestService georeferenceRequestService;

    @Autowired
    GeoreferenceValidationErrorsRepository georeferenceValidationErrorsRepository;

    GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public GeoreferenceRecord process(GeoreferenceRecord entity) {
        GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId).get();
        List<GeoreferenceRecordFail> errors = new ArrayList<>();
        GeoreferenceRecordFail rf = null;
        row++;
        if (entity.getFarmerName().equalsIgnoreCase("") || entity.getFarmerName().length() > 200) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("farmerName");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL NOMBRE CAFICULTOR EXCEDE LOS 200 CARACTERES");
            errors.add(rf);
        }
        if (entity.getDocumentType().equalsIgnoreCase("") || entity.getDocumentType().length() > 4) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("documentType");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL TIPO DE DOCUMENTO EXCEDE LA LONGITUD");
            errors.add(rf);
        }
        if (entity.getDocumentNumber().toString().length() > 15) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("documentNumber");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL NÚMERO DE DOCUMENTO EXCEDE LA LONGITUD");
            errors.add(rf);
        }
        if (entity.getFarmName().length() > 200) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("farmName");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL NOMBRE DE LA FINCA EXCEDE LOS 200 CARACTERES");
            errors.add(rf);
        }
        if (entity.getCultivationArea() == 0) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("cultivationName");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL ÁREA DE CULTIVO NO CUMPLE CON EL FORMATO");
            errors.add(rf);
        }
        if (entity.getMunicipalityCode().equalsIgnoreCase("")) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("municipalityCode");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL CÓDIGO DANE DEL MUNICIPIO NO EXISTE");
            errors.add(rf);
        }
        if (entity.getMunicipalityName().length() > 100) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("municipalityName");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL NOMBRE DEL MUNICIPIO EXCEDE LOS 100 CARACTERES");
            errors.add(rf);
        }
        if (entity.getDepartmentCode().equalsIgnoreCase("") || entity.getDepartmentCode().length() > 3) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("departmentCode");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL CÓDIGO DANE DEL DEPARTAMENTO NO EXISTE");
            errors.add(rf);
        }
        if (entity.getDepartmentName().length() > 100) {
            rf = new GeoreferenceRecordFail();
            rf.setGeoreferenceRequest(georeferenceRequest);
            rf.setColumnName("departmentName");
            rf.setRowNumber(row);
            rf.setErrorMessage("EL NOMBRE DEL DEPARTAMENTO EXCEDE LOS 100 CARACTERES");
            errors.add(rf);
        }
        georeferenceValidationErrorsRepository.saveAll(errors);
        if (errors.isEmpty()) {
            entity.setGeoreferenceRequest(georeferenceRequest);
            return entity;
        } else {
            errors.clear();
            return null;
        }
    }
}
