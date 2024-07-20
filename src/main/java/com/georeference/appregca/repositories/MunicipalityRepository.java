package com.georeference.appregca.repositories;

import com.georeference.appregca.entities.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {
    Municipality findByTxCodeDane(String txCodeDane);
}
