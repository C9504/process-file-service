package com.georeference.appregca.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

@Entity
@Table(name = "CM_DEPARTMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department {
    @Id
    @Column(name = "CD_DEPARTMENT")
    private Long cdDepartment;
    @Column(name = "TX_CODE_DANE")
    private String txCodeDane;
    @Column(name = "TX_NAME_DEPARTMENT")
    private String txNameDepartment;
}
