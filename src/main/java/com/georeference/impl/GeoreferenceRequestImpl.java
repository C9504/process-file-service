package com.georeference.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.process.repositories.GeoreferenceRequestRepository;
import com.georeference.services.GeoreferenceRequestService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GeoreferenceRequestImpl implements GeoreferenceRequestService {

    private final GeoreferenceRequestRepository georeferenceRequestRepository;
    private final EntityManager entityManager;

    @Override
    public List<GeoreferenceRequest> getAllGeoreferenceRequests() {
        return georeferenceRequestRepository.findAll();
    }

    @Override
    public GeoreferenceRequest getGeoreferenceRequestById(Long id) {
        return georeferenceRequestRepository.findById(id).orElse(null);
    }

    @Override
    public List<GeoreferenceRequest> getGeoreferenceRequestsByDocumentNumber(String documentNumber) {
        return georeferenceRequestRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    public GeoreferenceRequest saveGeoreferenceRequest(GeoreferenceRequest georeferenceRequest) {
        entityManager.persist(georeferenceRequest);
        entityManager.refresh(georeferenceRequest);
        return georeferenceRequest;
    }

    @Override
    public GeoreferenceRequest updateGeoreferenceRequest(GeoreferenceRequest georeferenceRequest) {
        return georeferenceRequestRepository.save(georeferenceRequest);
    }

    @Override
    public void deleteGeoreferenceRequest(Long id) {
        georeferenceRequestRepository.deleteById(id);
    }

    @Override
    public List<GeoreferenceRequest> getGeoreferenceRequestByLoadId(String loadId) {
        return georeferenceRequestRepository.findByLoadId(loadId);
    }

}
