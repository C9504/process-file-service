package com.georeference.appregca.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

@Entity
@Table(name = "CM_MUNICIPALITY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Municipality {
    @Id
    @Column(name = "CD_MUNICIPALITY")
    private Long cdMunicipality;
    @Column(name = "TX_CODE_DANE")
    private String txCodeDane;
    @Column(name = "TX_NAME_MUNICIPALITY")
    private String txNameMunicipality;
    @Column(name = "CD_DEPARTMENT")
    private Long cdDepartment;
}
