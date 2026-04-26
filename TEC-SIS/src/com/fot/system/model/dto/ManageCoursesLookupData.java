package com.fot.system.model.dto;

import com.fot.system.model.entity.Department;
import com.fot.system.model.entity.Staff;

import java.util.List;

/**
 * hold manage courses lookup datasets
 * @author janith
 */
public class ManageCoursesLookupData {
    private final List<Department> departments;
    private final List<Staff> lecturers;

    /**
     * initialize manage courses lookup payload
     * @author janith
     */
    public ManageCoursesLookupData(List<Department> departments, List<Staff> lecturers) {
        this.departments = departments;
        this.lecturers = lecturers;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public List<Staff> getLecturers() {
        return lecturers;
    }
}
