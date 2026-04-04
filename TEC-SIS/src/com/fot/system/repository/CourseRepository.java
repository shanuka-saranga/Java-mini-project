package com.fot.system.repository;

import com.fot.system.model.Course;
import com.fot.system.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        String sql = "SELECT * FROM course";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                c.setCredits(rs.getInt("credits"));
                c.setTotalHours(rs.getInt("total_hours"));
                c.setSessionType(rs.getString("session_type"));

                courses.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }
}