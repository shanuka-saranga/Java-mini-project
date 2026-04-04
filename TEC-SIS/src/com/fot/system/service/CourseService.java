package com.fot.system.service;

import com.fot.system.model.Course;
import com.fot.system.repository.CourseRepository;

import java.util.List;

public class CourseService {

    private final CourseRepository repo = new CourseRepository();

    public List<Course> getCourses() {
        return repo.getAllCourses();
    }
}
