package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.StudentCoursePerformance;
import com.fot.system.model.StudentsPerformance;
import com.fot.system.model.StudentCourseCaRecord;
import com.fot.system.model.StudentCourseGradeRecord;

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
            stmt.setInt(2, courseId);
            stmt.setInt(3, semesterYear);
            stmt.setInt(4, courseId);
            stmt.setInt(5, semesterYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCourseGradeRecord row = new StudentCourseGradeRecord();
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

    public List<StudentCoursePerformance> findAllSnapshotsByStudent(String regNo) {
        List<StudentCoursePerformance> snapshots = new ArrayList<>();

        return snapshots;
    }

    public List<StudentCoursePerformance> findSemesterSnapshotsByStudent(String regNo, int year) {
        return new ArrayList<>(); // මෙය සම්පූර්ණ කරන්න
    }
//
//    public List<StudentsPerformance> getAllStudentsAllCourseMarksWithAttendanceSummary() {
//        Map<String, StudentsPerformance> studentMap = new LinkedHashMap<>();
//
//        String sql = """
//    SELECT
//        s.registration_no,
//        CONCAT(u.first_name, ' ', u.last_name) AS full_name,
//        c.course_code,
//        c.course_name,
//        COALESCE(q.quiz_sum, 0) / NULLIF(c.no_of_quizzes, 0) AS quiz_avg,
//        COALESCE(a.assignment_sum, 0) AS assignment_total,
//        COALESCE(me.mid_mark, 0) AS mid_mark,
//        COALESCE(ee.end_mark, 0) AS end_mark,
//        COALESCE(att.present_count, 0) AS present_count,
//        COALESCE(att.absent_count, 0) AS absent_count,
//        COALESCE(att.medical_count, 0) AS medical_count,
//        COALESCE(att.total_attendance, 0) AS total_attendance
//    FROM student s
//    JOIN users u ON s.user_id = u.id
//    JOIN marks m ON s.registration_no = m.student_reg_no
//    JOIN courses c ON m.course_id = c.id
//    LEFT JOIN (
//        SELECT mark_id, SUM(quiz_mark) as quiz_sum
//        FROM quizzes
//        WHERE status = 'PRESENT'
//        GROUP BY mark_id
//    ) q ON q.mark_id = m.id
//    LEFT JOIN (
//        SELECT mark_id, SUM(assignment_mark) as assignment_sum
//        FROM assignments
//        WHERE status = 'SUBMITTED'
//        GROUP BY mark_id
//    ) a ON a.mark_id = m.id
//    LEFT JOIN (
//        SELECT mark_id, mid_exam_mark as mid_mark
//        FROM mid_exams
//        WHERE status = 'PRESENT'
//    ) me ON me.mark_id = m.id
//    LEFT JOIN (
//        SELECT mark_id, end_exam_mark as end_mark
//        FROM end_exams
//        WHERE status = 'PRESENT'
//    ) ee ON ee.mark_id = m.id
//    -- Attendance සඳහා නිවැරදි කළ කොටස
//    LEFT JOIN (
//        SELECT
//            att_inner.student_reg_no,
//            ts.course_id,
//            COUNT(CASE WHEN att_inner.attendance_status = 'PRESENT' THEN 1 END) as present_count,
//            COUNT(CASE WHEN att_inner.attendance_status = 'ABSENT' THEN 1 END) as absent_count,
//            COUNT(CASE WHEN att_inner.attendance_status = 'MEDICAL' THEN 1 END) as medical_count,
//            COUNT(*) as total_attendance
//        FROM attendance att_inner
//        JOIN sessions sess ON att_inner.session_id = sess.id
//        JOIN timetable_sessions ts ON sess.timetable_session_id = ts.id
//        GROUP BY att_inner.student_reg_no, ts.course_id
//    ) att ON att.student_reg_no = s.registration_no AND att.course_id = c.id
//    ORDER BY s.registration_no, c.course_code;
//    """;
//        try (Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                String regNo = rs.getString("registration_no");
//
//                StudentsPerformance studentRow = studentMap.computeIfAbsent(regNo, k -> {
//                    StudentsPerformance newStudent = new StudentsPerformance();
//                    newStudent.setRegNo(regNo);
//                    try {
//                        newStudent.setStudentName(rs.getString("full_name"));
//                    } catch (SQLException e) { e.printStackTrace(); }
//                    newStudent.setCourseMarks(new ArrayList<>());
//                    return newStudent;
//                });
//
//                StudentsPerformance.CourseMarksAndAttendanceDetail detail = new StudentsPerformance.CourseMarksAndAttendanceDetail();
//                detail.setCourseCode(rs.getString("course_code"));
//                detail.setCourseName(rs.getString("course_name"));
//                detail.setQuizAvg(rs.getDouble("quiz_avg"));
//                detail.setAssignmentTotal(rs.getDouble("assignment_total"));
//                detail.setMidExamMark(rs.getDouble("mid_mark"));
//                detail.setEndExamMark(rs.getDouble("end_mark"));
//                detail.setPresentCount(rs.getInt("present_count"));
//                detail.setAbsentCount(rs.getInt("absent_count"));
//                detail.setMedicalCount(rs.getInt("medical_count"));
//                detail.setTotalAttendance(rs.getInt("total_attendance"));
//
//                studentRow.getCourseMarks().add(detail);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Error fetching all student marks: " + e.getMessage(), e);
//        }
//
//        return new ArrayList<>(studentMap.values());
//    }
}
