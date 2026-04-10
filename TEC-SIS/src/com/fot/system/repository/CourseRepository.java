package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private final Connection conn;

    public CourseRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT id, course_code, course_name, credits, total_hours, session_type, department_id, lecturer_in_charge_id
                FROM courses
                ORDER BY course_code
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                c.setCredits(rs.getInt("credits"));
                c.setTotalHours(rs.getInt("total_hours"));
                c.setSessionType(rs.getString("session_type"));
                c.setDepartmentId(rs.getInt("department_id"));

                int lecturerId = rs.getInt("lecturer_in_charge_id");
                if (!rs.wasNull()) {
                    c.setLecturerInChargeId(lecturerId);
                }

                courses.add(c);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load courses: " + e.getMessage(), e);
        }

        return courses;
    }
}
