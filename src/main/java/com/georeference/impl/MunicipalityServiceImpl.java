package com.georeference.impl;

import com.georeference.appregca.entities.Municipality;
import com.georeference.appregca.repositories.MunicipalityRepository;
import com.georeference.services.MunicipalityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MunicipalityServiceImpl implements MunicipalityService {

    List<Municipality> municipalities;

    private final MunicipalityRepository municipalityRepository;

    MunicipalityServiceImpl(MunicipalityRepository municipalityRepository) {
        this.municipalityRepository = municipalityRepository;
        this.loadData();
    }

    public void loadData() {
        municipalities = municipalityRepository.getAllMunicipalities();
    }

    @Override
    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    @Override
    public Municipality getMunicipalityByCodes(String departmentCode, String municipalityCode) {
        Municipality municipality = null;
        for (Municipality municipality1 : municipalities) {
            if(Objects.equals(departmentCode, municipality1.getTxCodeDane()) && Objects.equals(municipalityCode, municipality1.getTxCodeDane())) {
                municipality = municipality1;
                break;
            }
        }
        return municipality; // O manejar el caso cuando no se encuentra el municipio
    }
}
