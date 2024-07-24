package com.georeference.services;

import com.georeference.process.entities.GeoreferenceRequest;

import java.util.List;
import java.util.Optional;

public interface GeoreferenceRequestService {
    List<GeoreferenceRequest> getAllGeoreferenceRequests();
    GeoreferenceRequest getGeoreferenceRequestById(Long id);
    List<GeoreferenceRequest> getGeoreferenceRequestsByDocumentNumber(String documentNumber);
    GeoreferenceRequest saveGeoreferenceRequest(GeoreferenceRequest georeferenceRequest);
    GeoreferenceRequest updateGeoreferenceRequest(GeoreferenceRequest georeferenceRequest);
    void deleteGeoreferenceRequest(Long id);
    //String getLoadId();
}
