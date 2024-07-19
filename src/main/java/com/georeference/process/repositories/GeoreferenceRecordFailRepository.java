package com.georeference.process.repositories;

import com.georeference.process.entities.GeoreferenceRecordFail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoreferenceRecordFailRepository extends JpaRepository<GeoreferenceRecordFail, Long> {
    @Query("SELECT grf FROM GeoreferenceRecordFail grf WHERE grf.georeferenceRequest.id = :recordRequestId")
    List<GeoreferenceRecordFail> getByRecordRequestId(Long recordRequestId);
}
