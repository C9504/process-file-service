package com.georeference.process.repositories;

import com.georeference.process.entities.GeoreferenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoreferenceRequestRepository extends JpaRepository<GeoreferenceRequest, Long> {
    @Query("SELECT gr FROM GeoreferenceRequest gr WHERE gr.exporterDocumentNumber = :documentNumber")
    List<GeoreferenceRequest> findByDocumentNumber(String documentNumber);
}
