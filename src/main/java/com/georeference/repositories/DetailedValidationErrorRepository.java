package com.georeference.repositories;

import com.georeference.entities.DetailedValidationError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailedValidationErrorRepository extends JpaRepository<DetailedValidationError, Long> {
}
