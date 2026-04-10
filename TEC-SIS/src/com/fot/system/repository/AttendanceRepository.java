package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.AbsentSessionOption;
import com.fot.system.model.AttendanceTableRow;
import com.fot.system.model.StudentMedicalRow;
import com.fot.system.model.StudentAttendance;
import com.fot.system.model.StudentSessionAttendanceRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {

    private final Connection conn;

    public AttendanceRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<AttendanceTableRow> findAttendanceRowsByCourse(int courseId) {
        List<AttendanceTableRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    ts.start_time,
                    ts.end_time,
                    ts.venue,
                    s.status AS session_status,
                    a.attendance_status,
                    m.approval_status
                FROM timetable_sessions ts
                INNER JOIN sessions s ON s.timetable_session_id = ts.id
                CROSS JOIN student st
                INNER JOIN users u ON u.id = st.user_id
                LEFT JOIN attendance a
                    ON a.session_id = s.id
                   AND a.student_reg_no = st.registration_no
                LEFT JOIN medicals m
                    ON m.session_id = s.id
                   AND m.student_reg_no = st.registration_no
                WHERE ts.course_id = ?
                ORDER BY s.session_date, s.session_no, st.registration_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceTableRow row = new AttendanceTableRow();
                    row.setRegistrationNo(rs.getString("registration_no"));
                    row.setStudentName(rs.getString("student_name"));
                    row.setSessionType(rs.getString("session_type"));
                    row.setSessionNo(rs.getInt("session_no"));
                    row.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    row.setSessionDay(rs.getString("session_day"));
                    row.setTimeRange(formatTimeRange(rs.getString("start_time"), rs.getString("end_time")));
                    row.setVenue(rs.getString("venue"));
                    row.setSessionStatus(valueOrEmpty(rs.getString("session_status")));
                    row.setAttendanceStatus(valueOrEmpty(rs.getString("attendance_status")));
                    row.setMedicalApprovalStatus(valueOrEmpty(rs.getString("approval_status")));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load attendance rows: " + e.getMessage(), e);
        }

        return rows;
    }

    private String formatTimeRange(String startTime, String endTime) {
        return valueOrEmpty(startTime) + " - " + valueOrEmpty(endTime);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public List<StudentSessionAttendanceRow> findStudentSessionAttendanceRows(int studentUserId) {
        List<StudentSessionAttendanceRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    COALESCE(s.actual_start_time, ts.start_time) AS start_time,
                    COALESCE(s.actual_end_time, ts.end_time) AS end_time,
                    ts.venue,
                    s.status AS session_status,
                    COALESCE(a.attendance_status, 'NOT_MARKED') AS attendance_status,
                    COALESCE(m.approval_status, '') AS medical_approval_status,
                    COALESCE(a.remarks, '') AS attendance_remarks
                FROM student st
                INNER JOIN marks mk ON mk.student_reg_no = st.registration_no
                INNER JOIN courses c ON c.id = mk.course_id
                INNER JOIN timetable_sessions ts ON ts.course_id = c.id
                INNER JOIN sessions s ON s.timetable_session_id = ts.id
                LEFT JOIN attendance a
                    ON a.session_id = s.id
                   AND a.student_reg_no = st.registration_no
                LEFT JOIN medicals m
                    ON m.session_id = s.id
                   AND m.student_reg_no = st.registration_no
                WHERE st.user_id = ?
                ORDER BY s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentSessionAttendanceRow row = new StudentSessionAttendanceRow();
                    row.setCourseCode(rs.getString("course_code"));
                    row.setCourseName(rs.getString("course_name"));
                    row.setSessionType(rs.getString("session_type"));
                    row.setSessionNo(rs.getInt("session_no"));
                    row.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    row.setSessionDay(rs.getString("session_day"));
                    row.setTimeRange(formatTimeRange(rs.getString("start_time"), rs.getString("end_time")));
                    row.setVenue(rs.getString("venue"));
                    row.setSessionStatus(valueOrEmpty(rs.getString("session_status")));
                    row.setAttendanceStatus(valueOrEmpty(rs.getString("attendance_status")));
                    row.setMedicalApprovalStatus(valueOrEmpty(rs.getString("medical_approval_status")));
                    row.setRemarks(valueOrEmpty(rs.getString("attendance_remarks")));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student attendance rows: " + e.getMessage(), e);
        }

        return rows;
    }

    public List<StudentMedicalRow> findStudentMedicalRows(int studentUserId) {
        List<StudentMedicalRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    m.submitted_date,
                    m.approval_status,
                    m.approved_at,
                    COALESCE(m.remarks, '') AS remarks,
                    m.medical_document
                FROM student st
                INNER JOIN medicals m ON m.student_reg_no = st.registration_no
                INNER JOIN sessions s ON s.id = m.session_id
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                WHERE st.user_id = ?
                ORDER BY s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentMedicalRow row = new StudentMedicalRow();
                    row.setCourseCode(rs.getString("course_code"));
                    row.setCourseName(rs.getString("course_name"));
                    row.setSessionType(rs.getString("session_type"));
                    row.setSessionNo(rs.getInt("session_no"));
                    row.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    row.setSubmittedDate(String.valueOf(rs.getDate("submitted_date")));
                    row.setApprovalStatus(valueOrEmpty(rs.getString("approval_status")));
                    row.setApprovedAt(rs.getTimestamp("approved_at") == null ? "" : String.valueOf(rs.getTimestamp("approved_at")));
                    row.setRemarks(valueOrEmpty(rs.getString("remarks")));
                    row.setMedicalDocument(valueOrEmpty(rs.getString("medical_document")));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student medical rows: " + e.getMessage(), e);
        }

        return rows;
    }

    public String findStudentRegistrationNoByUserId(int studentUserId) {
        String sql = "SELECT registration_no FROM student WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("registration_no");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student registration number: " + e.getMessage(), e);
        }

        return null;
    }

    public List<AbsentSessionOption> findAbsentSessionsForStudentByDateRange(int studentUserId, Date startDate, Date endDate) {
        List<AbsentSessionOption> rows = new ArrayList<>();
        String sql = """
                SELECT
                    s.id AS session_id,
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    COALESCE(s.actual_start_time, ts.start_time) AS start_time,
                    COALESCE(s.actual_end_time, ts.end_time) AS end_time,
                    ts.venue
                FROM student st
                INNER JOIN attendance a ON a.student_reg_no = st.registration_no
                INNER JOIN sessions s ON s.id = a.session_id
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                LEFT JOIN medicals m
                    ON m.student_reg_no = st.registration_no
                   AND m.session_id = s.id
                WHERE st.user_id = ?
                  AND a.attendance_status = 'ABSENT'
                  AND s.session_date BETWEEN ? AND ?
                  AND m.id IS NULL
                ORDER BY s.session_date, c.course_code, s.session_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AbsentSessionOption option = new AbsentSessionOption();
                    option.setSessionId(rs.getInt("session_id"));
                    option.setCourseCode(rs.getString("course_code"));
                    option.setCourseName(rs.getString("course_name"));
                    option.setSessionType(rs.getString("session_type"));
                    option.setSessionNo(rs.getInt("session_no"));
                    option.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    option.setSessionDay(rs.getString("session_day"));
                    option.setTimeRange(formatTimeRange(rs.getString("start_time"), rs.getString("end_time")));
                    option.setVenue(rs.getString("venue"));
                    rows.add(option);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load absent sessions: " + e.getMessage(), e);
        }

        return rows;
    }

    public void saveStudentMedicalSubmissions(int studentUserId, List<Integer> sessionIds, String medicalDocumentPath, Date submittedDate) {
        String registrationNo = findStudentRegistrationNoByUserId(studentUserId);
        if (registrationNo == null || registrationNo.trim().isEmpty()) {
            throw new RuntimeException("Student registration could not be found.");
        }

        String validationSql = """
                SELECT COUNT(*)
                FROM attendance a
                INNER JOIN student st ON st.registration_no = a.student_reg_no
                LEFT JOIN medicals m
                    ON m.student_reg_no = st.registration_no
                   AND m.session_id = a.session_id
                WHERE st.user_id = ?
                  AND a.session_id = ?
                  AND a.attendance_status = 'ABSENT'
                  AND m.id IS NULL
                """;

        String insertSql = """
                INSERT INTO medicals (student_reg_no, session_id, medical_document, submitted_date, approval_status, remarks)
                VALUES (?, ?, ?, ?, 'PENDING', '')
                """;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement validateStmt = conn.prepareStatement(validationSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Integer sessionId : sessionIds) {
                    if (sessionId == null || sessionId <= 0) {
                        throw new RuntimeException("Invalid session selected.");
                    }

                    validateStmt.setInt(1, studentUserId);
                    validateStmt.setInt(2, sessionId);
                    try (ResultSet rs = validateStmt.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) == 0) {
                            throw new RuntimeException("One or more selected sessions are not valid absent sessions.");
                        }
                    }

                    insertStmt.setString(1, registrationNo);
                    insertStmt.setInt(2, sessionId);
                    insertStmt.setString(3, medicalDocumentPath);
                    insertStmt.setDate(4, submittedDate);
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to save medical submissions: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save medical submissions: " + e.getMessage(), e);
        }
    }


    public List<StudentAttendance> findAllStudentAttendance() {
        List<StudentAttendance> attendanceList = new ArrayList<>();

        String sql = """
    SELECT 
        s.registration_no, 
        CONCAT(u.first_name, ' ', u.last_name) AS full_name, 
        c.course_code, 
        COUNT(a.id) AS total_sessions,
        SUM(CASE WHEN a.attendance_status = 'PRESENT' OR a.attendance_status = 'MEDICAL' THEN 1 ELSE 0 END) AS attended_count
    FROM student s
    JOIN users u ON s.user_id = u.id
    JOIN attendance a ON s.registration_no = a.student_reg_no
    JOIN sessions sess ON a.session_id = sess.id
    -- මෙතැනදී sess.course_id වෙනුවට ඔබේ වගුවේ ඇති සැබෑ column නම ලබා දෙන්න
    JOIN courses c ON sess.course_id = c.id 
    GROUP BY s.registration_no, u.first_name, u.last_name, c.course_code
    ORDER BY s.registration_no, c.course_code;
    """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StudentAttendance att = new StudentAttendance();
                att.setRegNo(rs.getString("registration_no"));
                att.setStudentName(rs.getString("full_name"));
                att.setCourseCode(rs.getString("course_code"));

                int total = rs.getInt("total_sessions");
                int attended = rs.getInt("attended_count");

                att.setTotalSessions(total);
                att.setAttendedSessions(attended);

                // Calculate percentage logic
                double percentage = (total > 0) ? (attended * 100.0 / total) : 0.0;
                att.setAttendancePercentage(percentage);

                attendanceList.add(att);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching attendance summary: " + e.getMessage(), e);
        }

        return attendanceList;
    }
}
