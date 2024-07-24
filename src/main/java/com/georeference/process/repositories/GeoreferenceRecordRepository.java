package com.georeference.process.repositories;

import com.georeference.process.entities.GeoreferenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoreferenceRecordRepository extends JpaRepository<GeoreferenceRecord, Long> {
    @Query("SELECT gr FROM GeoreferenceRecord gr WHERE gr.georeferenceRequest.id = :requestId")
    List<GeoreferenceRecord> getByRequestId(Long requestId);
}
