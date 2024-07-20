package com.georeference.batch;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.appregca.repositories.MunicipalityRepository;
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

@JobScope
@Component
public class GeoreferenceRecordProcessor implements ItemProcessor<GeoreferenceRecord, GeoreferenceRecord> {

    private static final String DEPARTMENT_DANE_CODE = "Código DANE Departamento";
    private static final String MUNICIPALITY_DANE_CODE = "Código DANE Municipio";
    private static final String FARMER_NAME = "Nombre del Caficultor";

    private final Long requestId;
    private int row = 1;

    private final GeoreferenceRequestService georeferenceRequestService;

    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;

    private final List<GeoreferenceRecordFail> errors = new ArrayList<>();

    GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId, GeoreferenceRequestService georeferenceRequestService, DepartmentRepository departmentRepository, MunicipalityRepository municipalityRepository) {
        this.georeferenceRequestService = georeferenceRequestService;
        this.requestId = requestId;
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
        validateFields(entity, georeferenceRequest, department, municipality);
        return entity;
    }

    private void addError(GeoreferenceRequest georeferenceRequest, String columnName, Integer rowNumber, String errorMessage) {
        GeoreferenceRecordFail rf = new GeoreferenceRecordFail();
        rf.setGeoreferenceRequest(georeferenceRequest);
        rf.setColumnName(columnName);
        rf.setRowNumber(rowNumber);
        rf.setErrorMessage(errorMessage);
        errors.add(rf);
    }

    private void validateFields(GeoreferenceRecord entity, GeoreferenceRequest georeferenceRequest, Department department, Municipality municipality) {
        if (entity.getFarmerName().equalsIgnoreCase("")) {
            addError(georeferenceRequest, FARMER_NAME, row, "EL NOMBRE CAFICULTOR ES REQUERIDO");
        }
        if (entity.getFarmerName().length() > 200) {
            addError(georeferenceRequest, FARMER_NAME, row, "EL NOMBRE CAFICULTOR EXCEDE LOS 200 CARACTERES");
        }
        if (entity.getDocumentType().equalsIgnoreCase("") || entity.getDocumentType().length() > 4) {
            addError(georeferenceRequest, "Tipo de Documento", row, "EL TIPO DE DOCUMENTO EXCEDE LA LONGITUD");
        }
        if (entity.getDocumentNumber().toString().length() > 15) {
            addError(georeferenceRequest, "Numero de Documento", row, "EL NÚMERO DE DOCUMENTO EXCEDE LA LONGITUD");
        }
        if (entity.getFarmName().length() > 200) {
            addError(georeferenceRequest, "Nombre de la Finca", row, "EL NOMBRE DE LA FINCA EXCEDE LOS 200 CARACTERES");
        }
        if (entity.getCultivationArea() == 0) {
            addError(georeferenceRequest, "Área de Cultivo", row, "EL ÁREA DE CULTIVO NO CUMPLE CON EL FORMATO");
        }
        if (entity.getDepartmentCode().equalsIgnoreCase("")) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "CÓDIGO DEPARTAMENTO ES REQUERIDO");
        }
        if (entity.getDepartmentCode().length() > 3) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "CÓDIGO DEPARTAMENTO EXCEDE LA LONGITUD");
        }
        if (department != null && !entity.getDepartmentCode().equals(department.getTxCodeDane())) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "EL CÓDIGO DANE DEL DEPARTAMENTO NO EXISTE");
        }
        if (entity.getDepartmentName().length() > 100) {
            addError(georeferenceRequest, "Departamento", row, "EL NOMBRE DEL DEPARTAMENTO EXCEDE LOS 100 CARACTERES");
        }
        if (entity.getMunicipalityCode().equalsIgnoreCase("")) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "CÓDIGO DEPARTAMENTO ES REQUERIDO");
        }
        if (entity.getMunicipalityCode().length() > 2) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "CÓDIGO DEPARTAMENTO EXCEDE LA LONGITUD");
        }
        if (municipality != null && !entity.getMunicipalityCode().equals(municipality.getTxCodeDane())) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "EL CÓDIGO DANE DEL MUNICIPIO NO EXISTE");
        }
        if (entity.getMunicipalityName().length() > 100) {
            addError(georeferenceRequest, "Municipio", row, "EL NOMBRE DEL MUNICIPIO EXCEDE LOS 100 CARACTERES");
        }
    }

    public List<GeoreferenceRecordFail> getErrorList() {
        return errors;
    }
}
