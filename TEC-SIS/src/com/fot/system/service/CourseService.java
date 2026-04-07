package com.fot.system.service;

import com.fot.system.model.AddCourseRequest;
import com.fot.system.model.Course;
import com.fot.system.model.Department;
import com.fot.system.model.EditCourseRequest;
import com.fot.system.model.Staff;
import com.fot.system.repository.CourseRepository;
import com.fot.system.repository.DepartmentRepository;

import java.util.List;
import java.util.Set;

public class CourseService {

    private static final Set<String> VALID_SESSION_TYPES = Set.of("THEORY", "PRACTICAL", "BOTH");

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public CourseService() {
        this.courseRepository = new CourseRepository();
        this.departmentRepository = new DepartmentRepository();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public int getCourseCount() {
        return courseRepository.countAll();
    }

    public Course getCourseByCode(String courseCode) {
        if (normalize(courseCode).isEmpty()) {
            throw new RuntimeException("Course code is required.");
        }
        return courseRepository.findByCourseCode(courseCode.trim().toUpperCase());
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Staff> getAllLecturers() {
        return courseRepository.findAllLecturers();
    }

    public Course addCourse(AddCourseRequest request) {
        Course validatedCourse = validate(createCourse(request), false);

        if (courseRepository.existsByCourseCode(validatedCourse.getCourseCode())) {
            throw new RuntimeException("Course code already exists.");
        }

        courseRepository.save(validatedCourse);
        return courseRepository.findByCourseCode(validatedCourse.getCourseCode());
    }

    public Course updateCourse(EditCourseRequest request) {
        Course course = createCourse(request);
        course.setId(request.getCourseId());

        if (course.getId() <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        Course validatedCourse = validate(course, true);

        if (courseRepository.existsByCourseCodeExcludingId(validatedCourse.getCourseCode(), validatedCourse.getId())) {
            throw new RuntimeException("Course code already exists.");
        }

        if (!courseRepository.update(validatedCourse)) {
            throw new RuntimeException("Course update failed.");
        }
        return courseRepository.findByCourseCode(validatedCourse.getCourseCode());
    }

    public void deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        if (!courseRepository.deleteById(courseId)) {
            throw new RuntimeException("Course delete failed.");
        }
    }

    private Course validate(Course course, boolean requireId) {
        if (course == null) {
            throw new RuntimeException("Course details are required.");
        }

        if (requireId && course.getId() <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        String courseCode = normalize(course.getCourseCode()).toUpperCase();
        if (courseCode.isEmpty()) {
            throw new RuntimeException("Course code is required.");
        }

        String courseName = normalize(course.getCourseName());
        if (courseName.isEmpty()) {
            throw new RuntimeException("Course name is required.");
        }

        if (course.getCredits() <= 0) {
            throw new RuntimeException("Credits must be greater than 0.");
        }

        if (course.getTotalHours() <= 0) {
            throw new RuntimeException("Total hours must be greater than 0.");
        }

        String sessionType = normalize(course.getSessionType()).toUpperCase();
        if (!VALID_SESSION_TYPES.contains(sessionType)) {
            throw new RuntimeException("Invalid session type.");
        }

        if (course.getDepartmentId() <= 0) {
            throw new RuntimeException("Department is required.");
        }

        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setSessionType(sessionType);
        return course;
    }

    private Course createCourse(AddCourseRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredits(parsePositiveInt(request.getCredits(), "Credits must be greater than 0."));
        course.setTotalHours(parsePositiveInt(request.getTotalHours(), "Total hours must be greater than 0."));
        course.setSessionType(request.getSessionType());
        course.setDepartmentId(parsePositiveInt(request.getDepartmentId(), "Department is required."));
        course.setLecturerInChargeId(parseOptionalInt(request.getLecturerInChargeId()));
        return course;
    }

    private int parsePositiveInt(String value, String message) {
        try {
            int parsedValue = Integer.parseInt(normalize(value));
            if (parsedValue <= 0) {
                throw new RuntimeException(message);
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    private Integer parseOptionalInt(String value) {
        String normalizedValue = normalize(value);
        if (normalizedValue.isEmpty()) {
            return null;
        }

        try {
            int parsedValue = Integer.parseInt(normalizedValue);
            if (parsedValue <= 0) {
                throw new RuntimeException("Lecturer is invalid.");
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Lecturer is invalid.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
