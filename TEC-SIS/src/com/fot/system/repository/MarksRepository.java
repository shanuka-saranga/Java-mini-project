package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MarksRepository {

    private final Connection conn;

    public MarksRepository() {
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


    public List<StudentCourseGradeRecord> findStudentCourseGradeRecords(int courseId, int semesterYear) {
        List<StudentCourseGradeRecord> rows = new ArrayList<>();
        String sql = """
                SELECT
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    c.credits,
                    ? AS semester_year,
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    st.registration_year,
                    c.session_type,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS exam_component_count
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
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN end_exam_mark ELSE 0 END) AS end_exam_total
                    FROM end_exams
                    GROUP BY mark_id
                ) ee
                    ON ee.mark_id = latest_marks.id
                ORDER BY st.registration_year, st.registration_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterYear);
            stmt.setInt(3, courseId);
            stmt.setInt(4, semesterYear);
            stmt.setInt(5, courseId);
            stmt.setInt(6, semesterYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCourseGradeRecord row = new StudentCourseGradeRecord();
                    row.setCourseId(rs.getInt("course_id"));
                    row.setCourseCode(rs.getString("course_code"));
                    row.setCourseName(rs.getString("course_name"));
                    row.setCredits(rs.getInt("credits"));
                    row.setSemesterYear(rs.getInt("semester_year"));
                    row.setRegistrationNo(rs.getString("registration_no"));
                    row.setStudentName(rs.getString("student_name"));
                    row.setRegistrationYear(rs.getInt("registration_year"));
                    row.setSessionType(rs.getString("session_type"));
                    row.setQuizTotal(rs.getDouble("quiz_total"));
                    row.setAssignmentTotal(rs.getDouble("assignment_total"));
                    row.setMidExamTotal(rs.getDouble("mid_exam_total"));
                    row.setEndExamTotal(rs.getDouble("end_exam_total"));
                    row.setQuizCount(rs.getInt("no_of_quizzes"));
                    row.setAssignmentCount(rs.getInt("no_of_assignments"));
                    row.setMidExamCount(rs.getInt("exam_component_count"));
                    row.setEndExamCount(rs.getInt("exam_component_count"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load grade records: " + e.getMessage(), e);
        }

        return rows;
    }

    public List<StudentCourseGradeRecord> findLatestGradeRecordsByStudentUserId(int studentUserId) {
        List<StudentCourseGradeRecord> rows = new ArrayList<>();
        String sql = """
                SELECT
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    c.credits,
                    m.semester_year,
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    st.registration_year,
                    c.session_type,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS exam_component_count,
                    COALESCE(att.total_sessions, 0) AS total_sessions,
                    COALESCE(att.attended_sessions, 0) AS attended_sessions
                FROM student st
                INNER JOIN users u ON u.id = st.user_id
                INNER JOIN (
                    SELECT m1.*
                    FROM marks m1
                    INNER JOIN (
                        SELECT course_id, semester_year, MAX(attempt_no) AS max_attempt
                        FROM marks
                        WHERE student_reg_no = (SELECT registration_no FROM student WHERE user_id = ?)
                        GROUP BY course_id, semester_year
                    ) latest
                        ON latest.course_id = m1.course_id
                       AND latest.semester_year = m1.semester_year
                       AND latest.max_attempt = m1.attempt_no
                    WHERE m1.student_reg_no = (SELECT registration_no FROM student WHERE user_id = ?)
                ) m ON m.student_reg_no = st.registration_no
                INNER JOIN courses c ON c.id = m.course_id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN quiz_mark ELSE 0 END) AS quiz_total
                    FROM quizzes
                    GROUP BY mark_id
                ) q ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total
                    FROM assignments
                    GROUP BY mark_id
                ) a ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total
                    FROM mid_exams
                    GROUP BY mark_id
                ) me ON me.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN end_exam_mark ELSE 0 END) AS end_exam_total
                    FROM end_exams
                    GROUP BY mark_id
                ) ee ON ee.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        st2.user_id,
                        ts.course_id,
                        COUNT(DISTINCT a2.session_id) AS total_sessions,
                        SUM(CASE
                            WHEN a2.attendance_status = 'PRESENT' THEN 1
                            WHEN a2.attendance_status = 'MEDICAL' THEN 1
                            ELSE 0
                        END) AS attended_sessions
                    FROM student st2
                    INNER JOIN attendance a2 ON a2.student_reg_no = st2.registration_no
                    INNER JOIN sessions s2 ON s2.id = a2.session_id
                    INNER JOIN timetable_sessions ts ON ts.id = s2.timetable_session_id
                    GROUP BY st2.user_id, ts.course_id
                ) att ON att.user_id = st.user_id AND att.course_id = c.id
                WHERE st.user_id = ?
                ORDER BY m.semester_year DESC, c.course_code
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            stmt.setInt(2, studentUserId);
            stmt.setInt(3, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCourseGradeRecord row = mapStudentCourseGradeRecord(rs);
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student grade records: " + e.getMessage(), e);
        }

        return rows;
    }

    public List<StudentCoursePerformance> findAllSnapshotsByStudent(String regNo) {
        List<StudentCoursePerformance> snapshots = new ArrayList<>();
        String sql = """
                SELECT
                    c.id AS course_id,
                    c.credits,
                    c.session_type,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS exam_component_count,
                    COALESCE(att.total_sessions, 0) AS total_sessions,
                    COALESCE(att.attended_sessions, 0) AS attended_sessions
                FROM (
                    SELECT m1.*
                    FROM marks m1
                    INNER JOIN (
                        SELECT course_id, semester_year, MAX(attempt_no) AS max_attempt
                        FROM marks
                        WHERE student_reg_no = ?
                        GROUP BY course_id, semester_year
                    ) latest
                        ON latest.course_id = m1.course_id
                       AND latest.semester_year = m1.semester_year
                       AND latest.max_attempt = m1.attempt_no
                    WHERE m1.student_reg_no = ?
                ) m
                INNER JOIN courses c ON c.id = m.course_id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN quiz_mark ELSE 0 END) AS quiz_total
                    FROM quizzes
                    GROUP BY mark_id
                ) q ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total
                    FROM assignments
                    GROUP BY mark_id
                ) a ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total
                    FROM mid_exams
                    GROUP BY mark_id
                ) me ON me.mark_id = m.id
                LEFT JOIN (
                    SELECT mark_id, SUM(CASE WHEN status = 'PRESENT' THEN end_exam_mark ELSE 0 END) AS end_exam_total
                    FROM end_exams
                    GROUP BY mark_id
                ) ee ON ee.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        a2.student_reg_no,
                        ts.course_id,
                        COUNT(DISTINCT a2.session_id) AS total_sessions,
                        SUM(CASE
                            WHEN a2.attendance_status = 'PRESENT' THEN 1
                            WHEN a2.attendance_status = 'MEDICAL' THEN 1
                            ELSE 0
                        END) AS attended_sessions
                    FROM attendance a2
                    INNER JOIN sessions s2 ON s2.id = a2.session_id
                    INNER JOIN timetable_sessions ts ON ts.id = s2.timetable_session_id
                    GROUP BY a2.student_reg_no, ts.course_id
                ) att ON att.student_reg_no = m.student_reg_no AND att.course_id = c.id
                ORDER BY m.semester_year DESC, c.course_code
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            stmt.setString(2, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCoursePerformance snapshot = new StudentCoursePerformance();
                    snapshot.setCourseId(rs.getInt("course_id"));
                    snapshot.setCredits(rs.getInt("credits"));
                    snapshot.setSessionType(rs.getString("session_type"));

                    int componentCount = rs.getInt("no_of_quizzes") + rs.getInt("no_of_assignments") + rs.getInt("exam_component_count");
                    double caAverage = componentCount <= 0
                            ? 0
                            : (rs.getDouble("quiz_total") + rs.getDouble("assignment_total") + rs.getDouble("mid_exam_total")) / componentCount;
                    double endExamAverage = rs.getInt("exam_component_count") <= 0
                            ? 0
                            : rs.getDouble("end_exam_total") / rs.getInt("exam_component_count");

                    snapshot.setCaMarks(caAverage);
                    snapshot.setEndExamMarks(endExamAverage);

                    int totalSessions = rs.getInt("total_sessions");
                    int attendedSessions = rs.getInt("attended_sessions");
                    snapshot.setTotalSessions(totalSessions);
                    snapshot.setPresentCount(attendedSessions);
                    snapshot.setAttendancePercentage(totalSessions <= 0 ? 0 : (attendedSessions * 100.0) / totalSessions);
                    snapshots.add(snapshot);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student performance snapshots: " + e.getMessage(), e);
        }

        return snapshots;
    }

    public List<StudentCoursePerformance> findSemesterSnapshotsByStudent(String regNo, int year) {
        return new ArrayList<>();
    }

    private StudentCourseGradeRecord mapStudentCourseGradeRecord(ResultSet rs) throws SQLException {
        StudentCourseGradeRecord row = new StudentCourseGradeRecord();
        row.setCourseId(rs.getInt("course_id"));
        row.setCourseCode(rs.getString("course_code"));
        row.setCourseName(rs.getString("course_name"));
        row.setCredits(rs.getInt("credits"));
        row.setSemesterYear(rs.getInt("semester_year"));
        row.setRegistrationNo(rs.getString("registration_no"));
        row.setStudentName(rs.getString("student_name"));
        row.setRegistrationYear(rs.getInt("registration_year"));
        row.setSessionType(rs.getString("session_type"));
        row.setQuizTotal(rs.getDouble("quiz_total"));
        row.setAssignmentTotal(rs.getDouble("assignment_total"));
        row.setMidExamTotal(rs.getDouble("mid_exam_total"));
        row.setEndExamTotal(rs.getDouble("end_exam_total"));
        row.setQuizCount(rs.getInt("no_of_quizzes"));
        row.setAssignmentCount(rs.getInt("no_of_assignments"));
        row.setMidExamCount(rs.getInt("exam_component_count"));
        row.setEndExamCount(rs.getInt("exam_component_count"));
        return row;
    }
}
