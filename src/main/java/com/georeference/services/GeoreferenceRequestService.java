package com.georeference.services;

import java.util.List;

import com.georeference.process.entities.GeoreferenceRequest;

public interface GeoreferenceRequestService {
    List<GeoreferenceRequest> getAllGeoreferenceRequests();
    GeoreferenceRequest getGeoreferenceRequestById(Long id);
    List<GeoreferenceRequest> getGeoreferenceRequestsByDocumentNumber(String documentNumber);
    List<GeoreferenceRequest> getGeoreferenceRequestByLoadId(String loadId);
    GeoreferenceRequest saveGeoreferenceRequest(GeoreferenceRequest georeferenceRequest);
    GeoreferenceRequest updateGeoreferenceRequest(GeoreferenceRequest georeferenceRequest);
    void deleteGeoreferenceRequest(Long id);
    //String getLoadId();
}
