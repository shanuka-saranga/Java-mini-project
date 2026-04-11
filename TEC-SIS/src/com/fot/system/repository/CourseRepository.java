package com.fot.system.repository;

import com.fot.system.config.AppConfig;
import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private final Connection conn;

    public CourseRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = baseCourseSelect() + " ORDER BY c.course_code";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load courses: " + e.getMessage(), e);
        }

        return courses;
    }

    public Course findByCourseCode(String courseCode) {
        String sql = baseCourseSelect() + " WHERE c.course_code = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load course: " + e.getMessage(), e);
        }

        return null;
    }

    public Course findById(int courseId) {
        String sql = baseCourseSelect() + " WHERE c.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load course: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Course> findByLecturerId(int lecturerId) {
        List<Course> courses = new ArrayList<>();
        String sql = baseCourseSelect() + " WHERE c.lecturer_in_charge_id = ? ORDER BY c.course_code";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load lecturer courses: " + e.getMessage(), e);
        }

        return courses;
    }

    public List<Course> findByStudentUserId(int studentUserId) {
        List<Course> courses = new ArrayList<>();
        String sql = baseCourseSelect() + """
                 INNER JOIN marks m ON m.course_id = c.id
                 INNER JOIN student s ON s.registration_no = m.student_reg_no
                 WHERE s.user_id = ?
                 GROUP BY c.id, c.course_code, c.course_name, c.credits, c.total_hours, c.session_type,
                          c.no_of_quizzes, c.no_of_assignments, c.department_id, d.dept_name,
                          c.lecturer_in_charge_id, lecturer_name
                 ORDER BY c.course_code
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student courses: " + e.getMessage(), e);
        }

        return courses;
    }

    public boolean save(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, credits, total_hours, session_type, no_of_quizzes, no_of_assignments, department_id, lecturer_in_charge_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindCourse(stmt, course);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save course: " + e.getMessage(), e);
        }
    }

    public boolean update(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, total_hours = ?, session_type = ?, no_of_quizzes = ?, no_of_assignments = ?, department_id = ?, lecturer_in_charge_id = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindCourse(stmt, course);
            stmt.setInt(10, course.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update course: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    public boolean existsByCourseCode(String courseCode) {
        return exists("SELECT 1 FROM courses WHERE course_code = ?", courseCode);
    }

    public boolean existsByCourseCodeExcludingId(String courseCode, int courseId) {
        return exists("SELECT 1 FROM courses WHERE course_code = ? AND id <> ?", courseCode, courseId);
    }

    public List<Staff> findAllLecturers() {
        List<Staff> lecturers = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, department_id FROM users WHERE role = ? ORDER BY first_name, last_name";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, AppConfig.ROLE_LECTURER);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Staff lecturer = new Staff();
                    lecturer.setId(rs.getInt("id"));
                    lecturer.setFirstName(rs.getString("first_name"));
                    lecturer.setLastName(rs.getString("last_name"));
                    lecturer.setDepartmentId(rs.getInt("department_id"));
                    lecturer.setRole(AppConfig.ROLE_LECTURER);
                    lecturers.add(lecturer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load lecturers: " + e.getMessage(), e);
        }

        return lecturers;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM courses";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count courses: " + e.getMessage(), e);
        }

        return 0;
    }

    private String baseCourseSelect() {
        return "SELECT c.id, c.course_code, c.course_name, c.credits, c.total_hours, c.session_type, c.no_of_quizzes, c.no_of_assignments, " +
                "c.department_id, d.dept_name, c.lecturer_in_charge_id, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS lecturer_name " +
                "FROM courses c " +
                "INNER JOIN departments d ON d.id = c.department_id " +
                "LEFT JOIN users u ON u.id = c.lecturer_in_charge_id";
    }

    private void bindCourse(PreparedStatement stmt, Course course) throws SQLException {
        stmt.setString(1, course.getCourseCode());
        stmt.setString(2, course.getCourseName());
        stmt.setInt(3, course.getCredits());
        stmt.setInt(4, course.getTotalHours());
        stmt.setString(5, course.getSessionType());
        stmt.setInt(6, course.getNoOfQuizzes());
        stmt.setInt(7, course.getNoOfAssignments());
        stmt.setInt(8, course.getDepartmentId());

        if (course.getLecturerInChargeId() == null) {
            stmt.setNull(9, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(9, course.getLecturerInChargeId());
        }
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setTotalHours(rs.getInt("total_hours"));
        course.setSessionType(rs.getString("session_type"));
        course.setNoOfQuizzes(rs.getInt("no_of_quizzes"));
        course.setNoOfAssignments(rs.getInt("no_of_assignments"));
        course.setDepartmentId(rs.getInt("department_id"));
        course.setDepartmentName(rs.getString("dept_name"));

        int lecturerId = rs.getInt("lecturer_in_charge_id");
        if (!rs.wasNull()) {
            course.setLecturerInChargeId(lecturerId);
        }
        course.setLecturerInChargeName(rs.getString("lecturer_name"));
        return course;
    }

    private boolean exists(String sql, String value) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database check failed: " + e.getMessage(), e);
        }
    }

    private boolean exists(String sql, String value, int courseId) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database check failed: " + e.getMessage(), e);
        }
    }

}
