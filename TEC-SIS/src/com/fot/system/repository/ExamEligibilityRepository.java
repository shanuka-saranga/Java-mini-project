package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.StudentCourseCaRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamEligibilityRepository {

    private final Connection conn;

    public ExamEligibilityRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<StudentCourseCaRecord> findStudentCourseCaRecords(int courseId, int semesterYear) {
        List<StudentCourseCaRecord> rows = new ArrayList<>();
        String sql = """
                SELECT
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    st.registration_year,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS mid_exam_count
                FROM student st
                INNER JOIN users u ON u.id = st.user_id
                INNER JOIN courses c ON c.id = ?
                LEFT JOIN (
                    SELECT m.student_reg_no, m.id
                    FROM marks m
                    INNER JOIN (
                        SELECT student_reg_no, MAX(attempt_no) AS max_attempt
                        FROM marks
                        WHERE course_id = ? AND semester_year = ?
                        GROUP BY student_reg_no
                    ) latest
                        ON latest.student_reg_no = m.student_reg_no
                       AND latest.max_attempt = m.attempt_no
                    WHERE m.course_id = ? AND m.semester_year = ?
                ) latest_marks
                    ON latest_marks.student_reg_no = st.registration_no
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN quiz_mark ELSE 0 END) AS quiz_total
                    FROM quizzes
                    GROUP BY mark_id
                ) q
                    ON q.mark_id = latest_marks.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total
                    FROM assignments
                    GROUP BY mark_id
                ) a
                    ON a.mark_id = latest_marks.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total
                    FROM mid_exams
                    GROUP BY mark_id
                ) me
                    ON me.mark_id = latest_marks.id
                ORDER BY st.registration_year, st.registration_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, semesterYear);
            stmt.setInt(4, courseId);
            stmt.setInt(5, semesterYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCourseCaRecord row = new StudentCourseCaRecord();
                    row.setRegistrationNo(rs.getString("registration_no"));
                    row.setStudentName(rs.getString("student_name"));
                    row.setRegistrationYear(rs.getInt("registration_year"));
                    row.setQuizTotal(rs.getDouble("quiz_total"));
                    row.setAssignmentTotal(rs.getDouble("assignment_total"));
                    row.setMidExamTotal(rs.getDouble("mid_exam_total"));
                    row.setQuizCount(rs.getInt("no_of_quizzes"));
                    row.setAssignmentCount(rs.getInt("no_of_assignments"));
                    row.setMidExamCount(rs.getInt("mid_exam_count"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load exam eligibility CA records: " + e.getMessage(), e);
        }

        return rows;
    }
}
