package com.georeference.impl;

import com.georeference.entities.GeoreferenceRequest;
import com.georeference.repositories.GeoreferenceRequestRepository;
import com.georeference.services.GeoreferenceRequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeoreferenceRequestImpl implements GeoreferenceRequestService {

    private final GeoreferenceRequestRepository georeferenceRequestRepository;

    public GeoreferenceRequestImpl(GeoreferenceRequestRepository georeferenceRequestRepository) {
        this.georeferenceRequestRepository = georeferenceRequestRepository;
    }

    @Override
    public List<GeoreferenceRequest> getAllGeoreferenceRequests() {
        return georeferenceRequestRepository.findAll();
    }

    @Override
    public Optional<GeoreferenceRequest> getGeoreferenceRequestById(Long id) {
        return georeferenceRequestRepository.findById(id);
    }

    @Override
    public List<GeoreferenceRequest> getGeoreferenceRequestsByDocumentNumber(String documentNumber) {
        return georeferenceRequestRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    public GeoreferenceRequest saveGeoreferenceRequest(GeoreferenceRequest georeferenceRequest) {
        return georeferenceRequestRepository.save(georeferenceRequest);
    }

    @Override
    public GeoreferenceRequest updateGeoreferenceRequest(GeoreferenceRequest georeferenceRequest) {
        return georeferenceRequestRepository.save(georeferenceRequest);
    }

    @Override
    public void deleteGeoreferenceRequest(Long id) {
        georeferenceRequestRepository.deleteById(id);
    }
}
