package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentRepository {

    private final Connection conn;

    public StudentRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<Student> findByCourseId(int courseId) {
        List<Student> students = new ArrayList<>();

        String sql = """
            SELECT DISTINCT u.*, s.registration_no, s.registration_year, s.student_type
            FROM student s
            INNER JOIN users u ON s.user_id = u.id
            INNER JOIN marks m ON s.registration_no = m.student_reg_no
            WHERE m.course_id = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding students by course ID: " + e.getMessage(), e);
        }
        return students;
    }

    public Student findByRegistrationNo(String regNo) {
        String sql = """
            SELECT u.*, s.registration_no, s.registration_year, s.student_type
            FROM student s
            INNER JOIN users u ON s.user_id = u.id
            WHERE s.registration_no = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> findAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = """
            SELECT u.*, s.registration_no, s.registration_year, s.student_type
            FROM student s
            INNER JOIN users u ON s.user_id = u.id
            """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    private Student mapToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();

        // User fields
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setProfilePicturePath(rs.getString("profile_picture_path"));
        student.setDob(rs.getDate("dob"));
        student.setDepartmentId(rs.getInt("department_id"));
        student.setRole(rs.getString("role"));
        student.setStatus(rs.getString("status"));

        // Student specific fields
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setRegistrationYear(rs.getInt("registration_year"));
        student.setStudentType(rs.getString("student_type"));

        return student;
    }


    public List<StudentsPerformance> getAllStudentsPerformance() {
        Map<String, StudentsPerformance> studentMap = new LinkedHashMap<>();

        String sql = """
        SELECT 
            s.registration_no,\s
            s.registration_year,\s
            student_type,\s
               u.first_name,\s
               u.last_name,\s
               u.email,
               u.phone,
               u.address,
               c.course_code,\s
               c.course_name,
               c.session_type,
            COALESCE(q.quiz_sum, 0) / NULLIF(c.no_of_quizzes, 0) AS quiz_avg,
            COALESCE(a.assignment_sum, 0) AS assignment_total,
            COALESCE(me.mid_mark, 0) AS mid_mark,
            COALESCE(ee.end_mark, 0) AS end_mark
        FROM student s
        JOIN users u ON s.user_id = u.id
        JOIN marks m ON s.registration_no = m.student_reg_no
        JOIN courses c ON m.course_id = c.id
        LEFT JOIN (
            SELECT mark_id, SUM(quiz_mark) as quiz_sum 
            FROM quizzes WHERE status = 'PRESENT' GROUP BY mark_id
        ) q ON q.mark_id = m.id
        LEFT JOIN (
            SELECT mark_id, SUM(assignment_mark) as assignment_sum 
            FROM assignments WHERE status = 'SUBMITTED' GROUP BY mark_id
        ) a ON a.mark_id = m.id
        LEFT JOIN (
            SELECT mark_id, mid_exam_mark as mid_mark 
            FROM mid_exams WHERE status = 'PRESENT'
        ) me ON me.mark_id = m.id
        LEFT JOIN (
            SELECT mark_id, end_exam_mark as end_mark 
            FROM end_exams WHERE status = 'PRESENT'
        ) ee ON ee.mark_id = m.id
        ORDER BY s.registration_no, c.course_code;
    """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String regNo = rs.getString("registration_no");

                StudentsPerformance studentRow = studentMap.computeIfAbsent(regNo, k -> {
                    StudentsPerformance sp = new StudentsPerformance();
                    sp.setRegistrationNo(regNo);
                    try {
                        sp.setFirstName(rs.getString("first_name"));
                        sp.setLastName(rs.getString("last_name"));
                        sp.setEmail(rs.getString("email"));
                        sp.setPhone(rs.getString("phone"));
                        sp.setAddress(rs.getString("address"));
                        sp.setRegistrationYear(rs.getInt("registration_year"));
                        sp.setStudentType(rs.getString("student_type"));

                    } catch (SQLException e) { e.printStackTrace(); }
                    return sp;
                });

                StudentsPerformance.CourseMarksAndAttendanceDetail detail = new StudentsPerformance.CourseMarksAndAttendanceDetail();
                detail.setCourseCode(rs.getString("course_code"));
                detail.setSessionType(rs.getString("session_type"));
                detail.setCourseName(rs.getString("course_name"));
                detail.setQuizAvg(rs.getDouble("quiz_avg"));
                detail.setAssignmentTotal(rs.getDouble("assignment_total"));
                detail.setMidExamMark(rs.getDouble("mid_mark"));
                detail.setEndExamMark(rs.getDouble("end_mark"));


                studentRow.getCourseMarks().add(detail);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching student marks: " + e.getMessage(), e);
        }
        return new ArrayList<>(studentMap.values());
    }
}
