package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.util.AcademicPerformance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksRepository {
    private static final boolean ENABLE_GRADE_FLOW_LOGS = true;

    private final Connection conn;
    private final AcademicPerformance academicPerformance;

    public MarksRepository() {
        this.conn = DBConnection.getInstance().getConnection();
        this.academicPerformance = new AcademicPerformance();
    }

    /**
     * Loads latest-attempt CA records for exam eligibility using quiz, assignment, and mid exam data only.
     * @param courseId selected course id
     * @param semesterYear selected semester year
     * @author janith
     */
    public List<StudentCourseCaRecord> findStudentCourseCaRecords(int courseId, int semesterYear) {
        List<StudentCourseCaRecord> rows = new ArrayList<>();
        String sql = """
                SELECT
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    st.registration_year,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(q.present_count, 0) AS quiz_present_count,
                    q.lowest_present_mark AS quiz_lowest_present_mark,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(a.submitted_count, 0) AS assignment_submitted_count,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(me.present_count, 0) AS mid_exam_present_count,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS mid_exam_count
                FROM student_course_registrations scr
                INNER JOIN (
                    SELECT student_user_id, MAX(attempt_no) AS max_attempt
                    FROM student_course_registrations
                    WHERE course_id = ?
                      AND semester_year = ?
                      AND registration_status = 'REGISTERED'
                    GROUP BY student_user_id
                ) latest_scr
                    ON latest_scr.student_user_id = scr.student_user_id
                   AND latest_scr.max_attempt = scr.attempt_no
                INNER JOIN users u
                    ON u.id = scr.student_user_id
                   AND u.role = 'STUDENT'
                INNER JOIN student st
                    ON st.user_id = scr.student_user_id
                INNER JOIN courses c
                    ON c.id = scr.course_id
                LEFT JOIN marks m
                    ON m.student_reg_no = st.registration_no
                   AND m.course_id = scr.course_id
                   AND m.semester_year = scr.semester_year
                   AND m.attempt_no = scr.attempt_no
                LEFT JOIN (
                    SELECT
                        q.mark_id,
                        SUM(CASE WHEN q.status = 'PRESENT' THEN q.quiz_mark ELSE 0 END) AS quiz_total,
                        SUM(CASE WHEN q.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        MIN(CASE WHEN q.status = 'PRESENT' THEN q.quiz_mark END) AS lowest_present_mark
                    FROM quizzes q
                    GROUP BY q.mark_id
                ) q
                    ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        a.mark_id,
                        SUM(CASE WHEN a.status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total,
                        SUM(CASE WHEN a.status = 'SUBMITTED' THEN 1 ELSE 0 END) AS submitted_count
                    FROM assignments a
                    GROUP BY a.mark_id
                ) a
                    ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        me.mark_id,
                        SUM(CASE WHEN me.status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total,
                        SUM(CASE WHEN me.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count
                    FROM mid_exams me
                    GROUP BY me.mark_id
                ) me
                    ON me.mark_id = m.id
                WHERE scr.course_id = ?
                  AND scr.semester_year = ?
                  AND scr.registration_status = 'REGISTERED'
                ORDER BY st.registration_year, st.registration_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterYear);
            stmt.setInt(3, courseId);
            stmt.setInt(4, semesterYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentCourseCaRecord row = new StudentCourseCaRecord();
                    row.setRegistrationNo(rs.getString("registration_no"));
                    row.setStudentName(rs.getString("student_name"));
                    row.setRegistrationYear(rs.getInt("registration_year"));
                    row.setQuizTotal(rs.getDouble("quiz_total"));
                    row.setQuizPresentCount(rs.getInt("quiz_present_count"));
                    row.setQuizLowestPresentMark(getNullableDouble(rs, "quiz_lowest_present_mark"));
                    row.setAssignmentTotal(rs.getDouble("assignment_total"));
                    row.setAssignmentSubmittedCount(rs.getInt("assignment_submitted_count"));
                    row.setMidExamTotal(rs.getDouble("mid_exam_total"));
                    row.setMidExamPresentCount(rs.getInt("mid_exam_present_count"));
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
        logGradeFlow("findStudentCourseGradeRecords.start -> courseId=" + courseId + ", semesterYear=" + semesterYear);
        String sql = """
                SELECT
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    c.credits,
                    scr.semester_year,
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    st.registration_year,
                    c.session_type,
                    scr.attempt_no,
                    COALESCE(q.quiz_total, 0) AS quiz_total,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    COALESCE(q.present_count, 0) AS quiz_present_count,
                    q.lowest_present_mark AS quiz_lowest_present_mark,
                    COALESCE(q.medical_count, 0) AS quiz_medical_count,
                    COALESCE(q.incomplete_count, 0) AS quiz_incomplete_count,
                    COALESCE(a.submitted_count, 0) AS assignment_submitted_count,
                    COALESCE(a.medical_count, 0) AS assignment_medical_count,
                    COALESCE(a.incomplete_count, 0) AS assignment_incomplete_count,
                    COALESCE(me.present_count, 0) AS mid_exam_present_count,
                    COALESCE(me.medical_count, 0) AS mid_exam_medical_count,
                    COALESCE(me.incomplete_count, 0) AS mid_exam_incomplete_count,
                    COALESCE(ee.present_count, 0) AS end_exam_present_count,
                    COALESCE(ee.medical_count, 0) AS end_exam_medical_count,
                    COALESCE(ee.incomplete_count, 0) AS end_exam_incomplete_count,
                    c.no_of_quizzes,
                    c.no_of_assignments,
                    CASE WHEN c.session_type = 'BOTH' THEN 2 ELSE 1 END AS exam_component_count
                FROM student_course_registrations scr
                INNER JOIN users u
                    ON u.id = scr.student_user_id
                   AND u.role = 'STUDENT'
                INNER JOIN student st
                    ON st.user_id = scr.student_user_id
                INNER JOIN courses c
                    ON c.id = scr.course_id
                LEFT JOIN marks m
                    ON m.student_reg_no = st.registration_no
                   AND m.course_id = scr.course_id
                   AND m.semester_year = scr.semester_year
                   AND m.attempt_no = scr.attempt_no
                LEFT JOIN (
                    SELECT
                        q.mark_id,
                        SUM(CASE WHEN q.status = 'PRESENT' THEN q.quiz_mark ELSE 0 END) AS quiz_total,
                        SUM(CASE WHEN q.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        MIN(CASE WHEN q.status = 'PRESENT' THEN q.quiz_mark END) AS lowest_present_mark,
                        SUM(CASE WHEN q.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                        SUM(CASE WHEN q.status IN ('ABSENT', 'PENDING') THEN 1 ELSE 0 END) AS incomplete_count
                    FROM quizzes q
                    GROUP BY q.mark_id
                ) q
                    ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        a.mark_id,
                        SUM(CASE WHEN a.status = 'SUBMITTED' THEN a.assignment_mark ELSE 0 END) AS assignment_total,
                        SUM(CASE WHEN a.status = 'SUBMITTED' THEN 1 ELSE 0 END) AS submitted_count,
                        SUM(CASE WHEN a.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                        SUM(CASE WHEN a.status IN ('PENDING', 'NOT_SUBMITTED') THEN 1 ELSE 0 END) AS incomplete_count
                    FROM assignments a
                    GROUP BY a.mark_id
                ) a
                    ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        me.mark_id,
                        SUM(CASE WHEN me.status = 'PRESENT' THEN me.mid_exam_mark ELSE 0 END) AS mid_exam_total,
                        SUM(CASE WHEN me.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        SUM(CASE WHEN me.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                        SUM(CASE WHEN me.status IN ('ABSENT', 'PENDING') THEN 1 ELSE 0 END) AS incomplete_count
                    FROM mid_exams me
                    GROUP BY me.mark_id
                ) me
                    ON me.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        ee.mark_id,
                        SUM(CASE WHEN ee.status = 'PRESENT' THEN ee.end_exam_mark ELSE 0 END) AS end_exam_total,
                        SUM(CASE WHEN ee.status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        SUM(CASE WHEN ee.status = 'MEDICAL' THEN 1 ELSE 0 END) AS medical_count,
                        SUM(CASE WHEN ee.status IN ('ABSENT', 'PENDING') THEN 1 ELSE 0 END) AS incomplete_count
                    FROM end_exams ee
                    GROUP BY ee.mark_id
                ) ee
                    ON ee.mark_id = m.id
                WHERE scr.course_id = ?
                  AND scr.semester_year = ?
                  AND scr.registration_status = 'REGISTERED'
                ORDER BY st.registration_year, st.registration_no, scr.attempt_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, semesterYear);
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
                    row.setAttemptNo(rs.getInt("attempt_no"));
                    row.setQuizTotal(rs.getDouble("quiz_total"));
                    row.setAssignmentTotal(rs.getDouble("assignment_total"));
                    row.setMidExamTotal(rs.getDouble("mid_exam_total"));
                    row.setEndExamTotal(rs.getDouble("end_exam_total"));
                    row.setQuizPresentCount(rs.getInt("quiz_present_count"));
                    row.setQuizLowestPresentMark(getNullableDouble(rs, "quiz_lowest_present_mark"));
                    row.setQuizMedicalCount(rs.getInt("quiz_medical_count"));
                    row.setQuizIncompleteCount(rs.getInt("quiz_incomplete_count"));
                    row.setAssignmentSubmittedCount(rs.getInt("assignment_submitted_count"));
                    row.setAssignmentMedicalCount(rs.getInt("assignment_medical_count"));
                    row.setAssignmentIncompleteCount(rs.getInt("assignment_incomplete_count"));
                    row.setMidExamPresentCount(rs.getInt("mid_exam_present_count"));
                    row.setMidExamMedicalCount(rs.getInt("mid_exam_medical_count"));
                    row.setMidExamIncompleteCount(rs.getInt("mid_exam_incomplete_count"));
                    row.setEndExamPresentCount(rs.getInt("end_exam_present_count"));
                    row.setEndExamMedicalCount(rs.getInt("end_exam_medical_count"));
                    row.setEndExamIncompleteCount(rs.getInt("end_exam_incomplete_count"));
                    row.setQuizCount(rs.getInt("no_of_quizzes"));
                    row.setAssignmentCount(rs.getInt("no_of_assignments"));
                    row.setMidExamCount(rs.getInt("exam_component_count"));
                    row.setEndExamCount(rs.getInt("exam_component_count"));
                    rows.add(row);
                }
                logGradeFlow("findStudentCourseGradeRecords.queryDone -> rowCount=" + rows.size());
            }
        } catch (SQLException e) {
            logGradeFlow("findStudentCourseGradeRecords.error -> " + e.getMessage());
            throw new RuntimeException("Failed to load grade records: " + e.getMessage(), e);
        }

        return rows;
    }

    private Double getNullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private void logGradeFlow(String message) {
        if (ENABLE_GRADE_FLOW_LOGS) {
            System.out.println("[GRADE-FLOW][REPOSITORY] " + message);
        }
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
                    COALESCE(q.present_count, 0) AS quiz_present_count,
                    q.lowest_present_mark AS quiz_lowest_present_mark,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(a.submitted_count, 0) AS assignment_submitted_count,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(me.present_count, 0) AS mid_exam_present_count,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    COALESCE(ee.present_count, 0) AS end_exam_present_count,
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
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN quiz_mark ELSE 0 END) AS quiz_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        MIN(CASE WHEN status = 'PRESENT' THEN quiz_mark END) AS lowest_present_mark
                    FROM quizzes
                    GROUP BY mark_id
                ) q ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total,
                        SUM(CASE WHEN status = 'SUBMITTED' THEN 1 ELSE 0 END) AS submitted_count
                    FROM assignments
                    GROUP BY mark_id
                ) a ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count
                    FROM mid_exams
                    GROUP BY mark_id
                ) me ON me.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN end_exam_mark ELSE 0 END) AS end_exam_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count
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
                    COALESCE(q.present_count, 0) AS quiz_present_count,
                    q.lowest_present_mark AS quiz_lowest_present_mark,
                    COALESCE(a.assignment_total, 0) AS assignment_total,
                    COALESCE(a.submitted_count, 0) AS assignment_submitted_count,
                    COALESCE(me.mid_exam_total, 0) AS mid_exam_total,
                    COALESCE(me.present_count, 0) AS mid_exam_present_count,
                    COALESCE(ee.end_exam_total, 0) AS end_exam_total,
                    COALESCE(ee.present_count, 0) AS end_exam_present_count,
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
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN quiz_mark ELSE 0 END) AS quiz_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count,
                        MIN(CASE WHEN status = 'PRESENT' THEN quiz_mark END) AS lowest_present_mark
                    FROM quizzes
                    GROUP BY mark_id
                ) q ON q.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'SUBMITTED' THEN assignment_mark ELSE 0 END) AS assignment_total,
                        SUM(CASE WHEN status = 'SUBMITTED' THEN 1 ELSE 0 END) AS submitted_count
                    FROM assignments
                    GROUP BY mark_id
                ) a ON a.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN mid_exam_mark ELSE 0 END) AS mid_exam_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count
                    FROM mid_exams
                    GROUP BY mark_id
                ) me ON me.mark_id = m.id
                LEFT JOIN (
                    SELECT
                        mark_id,
                        SUM(CASE WHEN status = 'PRESENT' THEN end_exam_mark ELSE 0 END) AS end_exam_total,
                        SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS present_count
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

                    Double quizAverage = academicPerformance.calculateQuizAverageForConfiguredCount(
                            rs.getDouble("quiz_total"),
                            getNullableDouble(rs, "quiz_lowest_present_mark"),
                            rs.getInt("quiz_present_count"),
                            rs.getInt("no_of_quizzes")
                    );
                    Double assignmentAverage = academicPerformance.calculateAssignmentAverageForConfiguredCount(
                            rs.getDouble("assignment_total"),
                            rs.getInt("no_of_assignments")
                    );
                    Double midExamAverage = academicPerformance.calculateExamAverageForConfiguredCount(
                            rs.getDouble("mid_exam_total"),
                            rs.getInt("exam_component_count")
                    );
                    double caAverage = academicPerformance.averageComponentScores(
                            quizAverage,
                            assignmentAverage,
                            midExamAverage
                    );
                    Double endExamAverage = academicPerformance.calculateExamAverageForConfiguredCount(
                            rs.getDouble("end_exam_total"),
                            rs.getInt("exam_component_count")
                    );

                    snapshot.setCaMarks(caAverage);
                    snapshot.setEndExamMarks(endExamAverage == null ? 0 : endExamAverage);

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
        row.setQuizPresentCount(rs.getInt("quiz_present_count"));
        row.setQuizLowestPresentMark(getNullableDouble(rs, "quiz_lowest_present_mark"));
        row.setAssignmentTotal(rs.getDouble("assignment_total"));
        row.setAssignmentSubmittedCount(rs.getInt("assignment_submitted_count"));
        row.setMidExamTotal(rs.getDouble("mid_exam_total"));
        row.setMidExamPresentCount(rs.getInt("mid_exam_present_count"));
        row.setEndExamTotal(rs.getDouble("end_exam_total"));
        row.setEndExamPresentCount(rs.getInt("end_exam_present_count"));
        row.setQuizCount(rs.getInt("no_of_quizzes"));
        row.setAssignmentCount(rs.getInt("no_of_assignments"));
        row.setMidExamCount(rs.getInt("exam_component_count"));
        row.setEndExamCount(rs.getInt("exam_component_count"));
        return row;
    }
}
