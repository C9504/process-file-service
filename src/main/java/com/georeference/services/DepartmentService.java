package com.georeference.services;

import com.georeference.appregca.entities.Department;

import java.util.List;

public interface DepartmentService {
    void loadData();
    List<Department> getDepartments();
    Department getDepartmentByCode(String departmentCode);

}
