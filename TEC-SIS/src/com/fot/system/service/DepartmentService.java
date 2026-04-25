package com.fot.system.service;

import com.fot.system.model.entity.Department;
import com.fot.system.repository.DepartmentRepository;

import java.util.List;

/**
 * handle department lookup use cases for view and form layers
 * @author janith
 */
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * initialize department service dependencies
     * @author janith
     */
    public DepartmentService() {
        this.departmentRepository = new DepartmentRepository();
    }

    /**
     * get all departments for dropdown and profile rendering
     * @author janith
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
