package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.CourseRepository;
import com.fot.system.repository.DepartmentRepository;

import java.util.List;
import java.util.Set;

/**
 * manage course business logic and validation
 * @author janith
 */
public class CourseService {

    private static final Set<String> VALID_SESSION_TYPES = Set.of("THEORY", "PRACTICAL", "BOTH");

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    /**
     * initialize course service dependencies
     * @author janith
     */
    public CourseService() {
        this.courseRepository = new CourseRepository();
        this.departmentRepository = new DepartmentRepository();
    }

    /**
     * get all courses
     * @author janith
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * get total course count
     * @author janith
     */
    public int getCourseCount() {
        return courseRepository.countAll();
    }

    /**
     * get courses assigned to lecturer
     * @param lecturerId lecturer user id
     * @author janith
     */
    public List<Course> getCoursesByLecturerId(int lecturerId) {
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer ID.");
        }
        return courseRepository.findByLecturerId(lecturerId);
    }

    /**
     * get total course count for lecturer
     * @param lecturerId lecturer user id
     * @author janith
     */
    public int getCourseCountByLecturerId(int lecturerId) {
        return getCoursesByLecturerId(lecturerId).size();
    }

    /**
     * get courses for a student by user id
     * @param studentUserId student user id
     * @author janith
     */
    public List<Course> getCoursesByStudentUserId(int studentUserId) {
        if (studentUserId <= 0) {
            throw new RuntimeException("Invalid student user ID.");
        }
        return courseRepository.findByStudentUserId(studentUserId);
    }

    /**
     * find course by course code
     * @param courseCode course code
     * @author janith
     */
    public Course getCourseByCode(String courseCode) {
        if (normalize(courseCode).isEmpty()) {
            throw new RuntimeException("Course code is required.");
        }
        return courseRepository.findByCourseCode(courseCode.trim().toUpperCase());
    }

    /**
     * find course by id
     * @param courseId course id
     * @author janith
     */
    public Course getCourseById(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return courseRepository.findById(courseId);
    }

    /**
     * get all departments for lookup
     * @author janith
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * get all lecturers for lookup
     * @author janith
     */
    public List<Staff> getAllLecturers() {
        return courseRepository.findAllLecturers();
    }

    /**
     * add new course after validation
     * @param request add course request payload
     * @author janith
     */
    public Course addCourse(AddCourseRequest request) {
        Course validatedCourse = validate(createCourse(request), false);

        if (courseRepository.existsByCourseCode(validatedCourse.getCourseCode())) {
            throw new RuntimeException("Course code already exists.");
        }

        courseRepository.save(validatedCourse);
        return courseRepository.findByCourseCode(validatedCourse.getCourseCode());
    }

    /**
     * update existing course after validation
     * @param request edit course request payload
     * @author janith
     */
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

    /**
     * delete course by id
     * @param courseId course id
     * @author janith
     */
    public void deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        if (!courseRepository.deleteById(courseId)) {
            throw new RuntimeException("Course delete failed.");
        }
    }

    /**
     * validate and normalize course entity values
     * @param course course entity
     * @param requireId require valid id for update flow
     * @author janith
     */
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

        if (course.getNoOfQuizzes() < 0) {
            throw new RuntimeException("Number of quizzes cannot be negative.");
        }

        if (course.getNoOfAssignments() < 0) {
            throw new RuntimeException("Number of assignments cannot be negative.");
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

    /**
     * create course entity from add/edit request payload
     * @param request add course request payload
     * @author janith
     */
    private Course createCourse(AddCourseRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredits(parsePositiveInt(request.getCredits(), "Credits must be greater than 0."));
        course.setTotalHours(parsePositiveInt(request.getTotalHours(), "Total hours must be greater than 0."));
        course.setSessionType(request.getSessionType());
        course.setNoOfQuizzes(parseNonNegativeInt(request.getNoOfQuizzes(), "Number of quizzes cannot be negative."));
        course.setNoOfAssignments(parseNonNegativeInt(request.getNoOfAssignments(), "Number of assignments cannot be negative."));
        course.setDepartmentId(parsePositiveInt(request.getDepartmentId(), "Department is required."));
        course.setLecturerInChargeId(parseOptionalInt(request.getLecturerInChargeId()));
        return course;
    }

    /**
     * parse positive integer value
     * @param value input value
     * @param message validation message
     * @author janith
     */
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

    /**
     * parse optional integer value
     * @param value input value
     * @author janith
     */
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

    /**
     * parse non-negative integer value
     * @param value input value
     * @param message validation message
     * @author janith
     */
    private int parseNonNegativeInt(String value, String message) {
        try {
            int parsedValue = Integer.parseInt(normalize(value));
            if (parsedValue < 0) {
                throw new RuntimeException(message);
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    /**
     * normalize text by trimming spaces
     * @param value input value
     * @author janith
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
