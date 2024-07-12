package com.georeference.config.batch;

import com.georeference.entities.GeoreferenceRecord;
import com.georeference.entities.GeoreferenceRecordFail;
import com.georeference.entities.GeoreferenceRequest;
import com.georeference.repositories.GeoreferenceValidationErrorsRepository;
import com.georeference.services.GeoreferenceRequestService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
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

    @Autowired
    GeoreferenceRequestService georeferenceRequestService;

    @Autowired
    GeoreferenceValidationErrorsRepository georeferenceValidationErrorsRepository;
    @Autowired
    private View error;

    GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public GeoreferenceRecord process(GeoreferenceRecord entity) {
        GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId).get();
        List<GeoreferenceRecordFail> errors = new ArrayList<>();
        if (entity.getFarmerName().equalsIgnoreCase("") || entity.getFarmerName().length() > 200) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "farmerName", 1, "EL NOMBRE CAFICULTOR EXCEDE LOS 200 CARACTERES"));
        } else
        if (entity.getDocumentType().equalsIgnoreCase("") || entity.getDocumentType().length() > 4) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "documentType", 1, "EL TIPO DE DOCUMENTO EXCEDE LA LONGITUD"));
        } else
        if (entity.getDocumentNumber().toString().length() > 15) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "documentNumber", 1, "EL NÚMERO DE DOCUMENTO EXCEDE LA LONGITUD"));
        } else
        if (entity.getFarmName().length() > 200) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "farmName", 1, "EL NOMBRE DE LA FINCA EXCEDE LOS 200 CARACTERES"));
        } else
        if (entity.getCultivationArea() == 0) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "cultivationName", 1, "EL ÁREA DE CULTIVO NO CUMPLE CON EL FORMATO"));
        } else
        if (entity.getMunicipalityCode().equalsIgnoreCase("")) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "municipalityCode", 1, "EL CÓDIGO DANE DEL MUNICIPIO NO EXISTE"));
        } else
        if (entity.getMunicipalityName().length() > 100) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "municipalityName", 1, "EL NOMBRE DEL MUNICIPIO EXCEDE LOS 100 CARACTERES"));
        } else
        if (entity.getDepartmentCode().equalsIgnoreCase("")) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "departmentCode", 1, "EL CÓDIGO DANE DEL DEPARTAMENTO NO EXISTE"));
        } else
        if (entity.getDepartmentName().length() > 100) {
            errors.add(new GeoreferenceRecordFail(georeferenceRequest, "departmentName", 1, "EL NOMBRE DEL DEPARTAMENTO EXCEDE LOS 100 CARACTERES"));
        }
        /*for (GeoreferenceRecordFail georeferenceRecordFail : errors) {
            System.out.println(georeferenceRecordFail.getErrorMessage());
        }*/
        System.out.println(errors);
        //georeferenceValidationErrorsRepository.saveAll(errors);
        return null;
        /*if (errors.isEmpty()) {
            entity.setGeoreferenceRequest(georeferenceRequest);
            return entity;
        } else {
            return null;
        }*/
    }
}
