package com.georeference.batch;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.appregca.repositories.MunicipalityRepository;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.process.repositories.GeoreferenceRecordFailRepository;
import com.georeference.services.GeoreferenceRequestService;
import com.georeference.utils.BatchException;
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

    @Autowired
    GeoreferenceRequestService georeferenceRequestService;

    @Autowired
    GeoreferenceRecordFailRepository recordFailRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    MunicipalityRepository municipalityRepository;

    @Autowired
    GeoreferenceRecordFailRepository georeferenceRecordFailRepository;

    private final List<GeoreferenceRecordFail> errors = new ArrayList<>();

    GeoreferenceRecordProcessor(@Value("#{jobParameters['requestId']}") Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public GeoreferenceRecord process(GeoreferenceRecord entity) {
        GeoreferenceRequest georeferenceRequest = georeferenceRequestService.getGeoreferenceRequestById(requestId).get();
        GeoreferenceRecordFail rf = null;
        Department department = departmentRepository.findById(Long.parseLong(entity.getDepartmentCode())).get();
        Municipality municipality = municipalityRepository.findById(Long.parseLong(entity.getMunicipalityCode())).get();
        row++;
        try {
            if (entity.getFarmerName().equalsIgnoreCase("") || entity.getFarmerName().length() > 200) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Nombre del caficultor");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL NOMBRE CAFICULTOR EXCEDE LOS 200 CARACTERES");
                errors.add(rf);
            }
            if (entity.getDocumentType().equalsIgnoreCase("") || entity.getDocumentType().length() > 4) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Tipo de Documento");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL TIPO DE DOCUMENTO EXCEDE LA LONGITUD");
                errors.add(rf);
            }
            if (entity.getDocumentNumber().toString().length() > 15) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Numero de Documento");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL NÚMERO DE DOCUMENTO EXCEDE LA LONGITUD");
                errors.add(rf);
            }
            if (entity.getFarmName().length() > 200) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Nombre de la Finca");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL NOMBRE DE LA FINCA EXCEDE LOS 200 CARACTERES");
                errors.add(rf);
            }
            if (entity.getCultivationArea() == 0) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Área de Cultivo");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL ÁREA DE CULTIVO NO CUMPLE CON EL FORMATO");
                errors.add(rf);
            }
            if (entity.getDepartmentCode().equals(department.getTxCodeDane())) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Codigo Dane Departamento");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL CÓDIGO DANE DEL DEPARTAMENTO NO EXISTE");
                errors.add(rf);
            }
            if (entity.getDepartmentName().length() > 100) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Departamento");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL NOMBRE DEL DEPARTAMENTO EXCEDE LOS 100 CARACTERES");
                errors.add(rf);
            }
            if (entity.getMunicipalityCode().equals(municipality.getTxCodeDane())) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Codigo Dane Municipio");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL CÓDIGO DANE DEL MUNICIPIO NO EXISTE");
                errors.add(rf);
            }
            if (entity.getMunicipalityName().length() > 100) {
                rf = new GeoreferenceRecordFail();
                rf.setGeoreferenceRequest(georeferenceRequest);
                rf.setColumnName("Municipio");
                rf.setRowNumber(row);
                rf.setErrorMessage("EL NOMBRE DEL MUNICIPIO EXCEDE LOS 100 CARACTERES");
                errors.add(rf);
            }
            entity.setGeoreferenceRequest(georeferenceRequest);
            return entity;
        } catch (BatchException e) {
            georeferenceRequest.setStatus("CON ERROR");
            georeferenceRequestService.updateGeoreferenceRequest(georeferenceRequest);
            return null;
        }
        /**if (errors.isEmpty()) {
         entity.setGeoreferenceRequest(georeferenceRequest);
         return entity;
         } else {
         georeferenceRequest.setStatus("CON ERROR");
         georeferenceRequestService.updateGeoreferenceRequest(georeferenceRequest);
         errors.clear();
         return null;
         }*/
    }

    public List<GeoreferenceRecordFail> getErrorList() {
        return errors;
    }
}
