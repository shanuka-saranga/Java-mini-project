package com.fot.system.model.dto;

import com.fot.system.model.entity.Course;
import com.fot.system.model.entity.Staff;

import java.util.List;

public class TimetableLookupData {
    private final List<Course> courses;
    private final List<Staff> lecturers;

    public TimetableLookupData(List<Course> courses, List<Staff> lecturers) {
        this.courses = courses;
        this.lecturers = lecturers;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Staff> getLecturers() {
        return lecturers;
    }
}
