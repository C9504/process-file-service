package com.georeference.appregca.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CM_DEPARTMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @Column(name = "CD_DEPARTMENT")
    private Long cdDepartment;
    @Column(name = "TX_CODE_DANE")
    private String txCodeDane;
    @Column(name = "TX_NAME_DEPARTMENT")
    private String txNameDepartment;
}
