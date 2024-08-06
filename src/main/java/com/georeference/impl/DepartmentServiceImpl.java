package com.georeference.impl;

import com.georeference.appregca.entities.Department;
import com.georeference.appregca.repositories.DepartmentRepository;
import com.georeference.services.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    List<Department> departments;

    private final DepartmentRepository departmentRepository;

    DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
        this.loadData();
    }

    public void loadData() {
        departments = departmentRepository.findAll();
    }

    @Override
    public List<Department> getDepartments() {
        return departments;
    }

    @Override
    public Department getDepartmentByCode(String departmentCode) {
        Department department = null;
        for (Department department1 : departments) {
            if(Objects.equals(departmentCode, department1.getTxCodeDane())) {
                department = department1;
                break;
            }
        }
        return department; // O manejar el caso cuando no se encuentra el municipio
    }
}
