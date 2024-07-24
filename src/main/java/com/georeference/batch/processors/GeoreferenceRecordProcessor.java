package com.georeference.batch.processors;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.batch.validations.GeoreferenceValidator;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.services.DepartmentService;
import com.georeference.services.GeoreferenceRequestService;
import com.georeference.services.MunicipalityService;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@JobScope
@Component
public class GeoreferenceRecordProcessor implements ItemProcessor<GeoreferenceRecord, GeoreferenceRecord> {

    private final Long requestId;
    private int row = 1;

    private final GeoreferenceValidator validator;
    private final GeoreferenceRequestService georeferenceRequestService;

    @Autowired
    private MunicipalityService municipalityService;

    @Autowired
    private DepartmentService departmentService;

    private List<GeoreferenceRecordFail> errors = new ArrayList<>();

    public GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId, GeoreferenceValidator validator, GeoreferenceRequestService georeferenceRequestService) {
        this.georeferenceRequestService = georeferenceRequestService;
        this.requestId = requestId;
        this.validator = validator;
    }

    @Override
    public GeoreferenceRecord process(GeoreferenceRecord entity) {
        GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId);
        Department department = departmentService.getDepartmentByCode(entity.getDepartmentCode());
        Municipality municipality = municipalityService.getMunicipalityByCodes(department.getTxCodeDane(), entity.getMunicipalityCode());
        row++;
        entity.setGeoreferenceRequest(georeferenceRequest);
        errors = validator.validateFields(entity, georeferenceRequest, department, municipality, row);
        if (!errors.isEmpty() && georeferenceRequest != null) {
            georeferenceRequest.setStatus("CON ERROR");
            georeferenceRequestService.updateGeoreferenceRequest(georeferenceRequest);
        }
        return entity;
    }

    public List<GeoreferenceRecordFail> getErrorList() {
        return errors;
    }
}
