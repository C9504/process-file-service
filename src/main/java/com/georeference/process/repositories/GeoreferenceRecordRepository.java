package com.georeference.process.repositories;

import com.georeference.process.entities.GeoreferenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoreferenceRecordRepository extends JpaRepository<GeoreferenceRecord, Long> {
}
