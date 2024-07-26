package com.georeference.batch.validations;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.process.entities.GeoreferenceRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeoreferenceValidator {

    private static final String DEPARTMENT_DANE_CODE = "Código DANE Departamento";
    private static final String MUNICIPALITY_DANE_CODE = "Código DANE Municipio";
    private static final String FARMER_NAME = "Nombre del Caficultor";

    private final List<GeoreferenceRecordFail> errors;
    private int row;

    public GeoreferenceValidator() {
        this.errors = new ArrayList<>();
        this.row = 0;
    }

    public List<GeoreferenceRecordFail> validateFields(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest, Department department, Municipality municipality, int row) {
        this.row = row;
        validateFarmerName(georeferenceRecord, georeferenceRequest);
        validateFarmName(georeferenceRecord, georeferenceRequest);
        validateDocumentType(georeferenceRecord, georeferenceRequest);
        validateDocumentNumber(georeferenceRecord, georeferenceRequest);
        validateCultivateArea(georeferenceRecord, georeferenceRequest);
        validateDepartment(georeferenceRecord, georeferenceRequest, department);
        validateMunicipality(georeferenceRecord, georeferenceRequest, municipality);
        return errors;
    }

    private void validateFarmerName(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest) {
        if (georeferenceRecord.getFarmerName().equalsIgnoreCase("")) {
            addError(georeferenceRequest, FARMER_NAME, row, "EL NOMBRE CAFICULTOR ES REQUERIDO");
        }
        if (georeferenceRecord.getFarmerName().length() > 200) {
            addError(georeferenceRequest, FARMER_NAME, row, "EL NOMBRE CAFICULTOR EXCEDE LOS 200 CARACTERES");
        }
    }

    private void validateDocumentType(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest) {
        if (georeferenceRecord.getDocumentType().equalsIgnoreCase("") || georeferenceRecord.getDocumentType().length() > 4) {
            addError(georeferenceRequest, "Tipo de Documento", row, "EL TIPO DE DOCUMENTO EXCEDE LA LONGITUD");
        }
    }

    private void validateDocumentNumber(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest) {
        if (georeferenceRecord.getDocumentNumber().toString().length() > 15) {
            addError(georeferenceRequest, "Numero de Documento", row, "EL NÚMERO DE DOCUMENTO EXCEDE LA LONGITUD");
        }
    }

    private void validateFarmName(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest) {
        if (georeferenceRecord.getFarmName().length() > 200) {
            addError(georeferenceRequest, "Nombre de la Finca", row, "EL NOMBRE DE LA FINCA EXCEDE LOS 200 CARACTERES");
        }
    }

    private void validateCultivateArea(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest) {
        if (georeferenceRecord.getCultivationArea() == 0) {
            addError(georeferenceRequest, "Área de Cultivo", row, "EL ÁREA DE CULTIVO NO CUMPLE CON EL FORMATO");
        }
    }

    private void validateDepartment(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest, Department department) {
        if (georeferenceRecord.getDepartmentCode().equalsIgnoreCase("")) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "CÓDIGO DEPARTAMENTO ES REQUERIDO");
        }
        if (georeferenceRecord.getDepartmentCode().length() > 3) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "CÓDIGO DEPARTAMENTO EXCEDE LA LONGITUD");
        }
        if (department != null && !georeferenceRecord.getDepartmentCode().equals(department.getTxCodeDane())) {
            addError(georeferenceRequest, DEPARTMENT_DANE_CODE, row, "EL CÓDIGO DANE DEL DEPARTAMENTO NO EXISTE");
        }
        if (georeferenceRecord.getDepartmentName().length() > 100) {
            addError(georeferenceRequest, "Departamento", row, "EL NOMBRE DEL DEPARTAMENTO EXCEDE LOS 100 CARACTERES");
        }
    }

    private void validateMunicipality(GeoreferenceRecord georeferenceRecord, GeoreferenceRequest georeferenceRequest, Municipality municipality) {
        if (georeferenceRecord.getMunicipalityCode().equalsIgnoreCase("")) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "CÓDIGO MUNICIPAL ES REQUERIDO");
        }
        if (georeferenceRecord.getMunicipalityCode().length() > 3) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "CÓDIGO MUNICIPAL EXCEDE LA LONGITUD");
        }
        if (municipality != null && !georeferenceRecord.getMunicipalityCode().equals(municipality.getTxCodeDane())) {
            addError(georeferenceRequest, MUNICIPALITY_DANE_CODE, row, "EL CÓDIGO DANE DEL MUNICIPIO NO EXISTE");
        }
        if (georeferenceRecord.getMunicipalityName().length() > 100) {
            addError(georeferenceRequest, "Municipio", row, "EL NOMBRE DEL MUNICIPIO EXCEDE LOS 100 CARACTERES");
        }
    }

    private void addError(GeoreferenceRequest georeferenceRequest, String columnName, Integer rowNumber, String errorMessage) {
        GeoreferenceRecordFail rf = new GeoreferenceRecordFail();
        rf.setGeoreferenceRequest(georeferenceRequest);
        rf.setColumnName(columnName);
        rf.setRowNumber(rowNumber);
        rf.setErrorMessage(errorMessage);
        errors.add(rf);
    }

}
