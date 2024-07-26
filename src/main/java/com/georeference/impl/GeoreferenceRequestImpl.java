package com.georeference.impl;

import com.georeference.process.entities.GeoreferenceRequest;
import com.georeference.process.repositories.GeoreferenceRequestRepository;
import com.georeference.services.GeoreferenceRequestService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GeoreferenceRequestImpl implements GeoreferenceRequestService {

    private final GeoreferenceRequestRepository georeferenceRequestRepository;
    @Autowired
    private EntityManager entityManager;

    public GeoreferenceRequestImpl(GeoreferenceRequestRepository georeferenceRequestRepository) {
        this.georeferenceRequestRepository = georeferenceRequestRepository;
    }

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

}
