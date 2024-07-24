package com.georeference.services;

import com.georeference.appregca.entities.Municipality;

import java.util.List;

public interface MunicipalityService {
    void loadData();
    List<Municipality> getMunicipalities();
    Municipality getMunicipalityByCodes(String departmentCode, String municipalityCode);
}
