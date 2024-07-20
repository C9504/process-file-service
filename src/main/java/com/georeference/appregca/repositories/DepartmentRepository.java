package com.georeference.appregca.repositories;

import com.georeference.appregca.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByTxCodeDane(String txCodeDane);
}
