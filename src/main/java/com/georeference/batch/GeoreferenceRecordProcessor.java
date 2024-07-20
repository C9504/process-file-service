package com.georeference.batch;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.appregca.repositories.MunicipalityRepository;
import com.georeference.batch.validations.GeoreferenceValidator;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.services.GeoreferenceRequestService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JobScope
@Component
public class GeoreferenceRecordProcessor implements ItemProcessor<GeoreferenceRecord, GeoreferenceRecord> {

    private final Long requestId;
    private int row = 1;

    private final GeoreferenceValidator validator;
    private final GeoreferenceRequestService georeferenceRequestService;
    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;

    private List<GeoreferenceRecordFail> errors = new ArrayList<>();

    GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId, GeoreferenceValidator validator, GeoreferenceRequestService georeferenceRequestService, DepartmentRepository departmentRepository, MunicipalityRepository municipalityRepository) {
        this.georeferenceRequestService = georeferenceRequestService;
        this.requestId = requestId;
        this.validator = validator;
        this.departmentRepository = departmentRepository;
        this.municipalityRepository = municipalityRepository;
    }

    @Override
    public GeoreferenceRecord process(GeoreferenceRecord entity) {
        GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId).orElse(null);
        Department department = departmentRepository.findByTxCodeDane(entity.getDepartmentCode());
        Municipality municipality = municipalityRepository.findByTxCodeDane(entity.getMunicipalityCode());
        row++;
        entity.setGeoreferenceRequest(georeferenceRequest);
        this.errors = validator.validateFields(entity, georeferenceRequest, department, municipality, row);
        if (!this.errors.isEmpty()) {
            Objects.requireNonNull(georeferenceRequest).setStatus("CON ERROR");
            georeferenceRequestService.updateGeoreferenceRequest(georeferenceRequest);
        }
        return entity;
    }

    public List<GeoreferenceRecordFail> getErrorList() {
        return errors;
    }
}
