package com.georeference.repositories;

import com.georeference.entities.CsvUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvUploadRepository extends JpaRepository<CsvUpload, Long> {
}
