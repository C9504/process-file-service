package com.georeference.appregca.repositories;

import com.georeference.appregca.entities.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {
    @Query("SELECT mcp FROM Municipality mcp WHERE mcp.txCodeDane = :txCodeDane AND mcp.cdDepartment = :cdDepartment ORDER BY mcp.cdMunicipality ASC")
    List<Municipality> findByTxCodeDane(@Param("txCodeDane") String txCodeDane, @Param("cdDepartment") Long cdDepartment);

    @Query("SELECT mcp FROM Municipality mcp")
    List<Municipality> getAllMunicipalities();
}
