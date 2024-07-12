package com.georeference.repositories;

import com.georeference.entities.GeoreferenceRecordFail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoreferenceValidationErrorsRepository extends JpaRepository<GeoreferenceRecordFail, GeoreferenceRecordFail> {
}
