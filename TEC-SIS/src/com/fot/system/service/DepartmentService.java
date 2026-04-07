package com.fot.system.service;

import com.fot.system.model.Department;
import com.fot.system.repository.DepartmentRepository;

import java.util.List;

public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService() {
        this.departmentRepository = new DepartmentRepository();
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
