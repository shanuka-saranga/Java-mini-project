package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles database operations for attendance sessions, attendance rows, and medical links.
 * @author methum
 */
public class AttendanceRepository {

    private final Connection conn;

    /**
     * Initializes the attendance repository.
     * @author methum
     */
    public AttendanceRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * load attendance rows for lecturer course table
     * @param courseId course id
     * @author poornika
     */
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
                    COALESCE(MAX(CASE WHEN m.approval_status = 'APPROVED' THEN 'APPROVED' ELSE m.approval_status END), '') AS approval_status
                FROM timetable_sessions ts
                INNER JOIN sessions s ON s.timetable_session_id = ts.id
                CROSS JOIN student st
                INNER JOIN users u ON u.id = st.user_id
                LEFT JOIN attendance a
                    ON a.session_id = s.id
                   AND a.student_reg_no = st.registration_no
                LEFT JOIN medical_sessions ms
                    ON ms.session_id = s.id
                LEFT JOIN medicals m
                    ON m.id = ms.medical_id
                   AND m.student_reg_no = st.registration_no
                WHERE ts.course_id = ?
                GROUP BY st.registration_no, student_name, ts.session_type, s.session_no, s.session_date,
                         ts.session_day, ts.start_time, ts.end_time, ts.venue, s.status, a.attendance_status
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

    /**
     * format session time range text
     * @param startTime start time
     * @param endTime end time
     * @author poornika
     */
    private String formatTimeRange(String startTime, String endTime) {
        return valueOrEmpty(startTime) + " - " + valueOrEmpty(endTime);
    }

    /**
     * normalize nullable values for DTO mapping
     * @param value raw value
     * @author poornika
     */
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * load attendance sessions filtered by lecturer
     * @param lecturerId lecturer user id
     * @author poornika
     */
    public List<AttendanceSessionRow> findAttendanceSessionsByLecturer(int lecturerId) {
        List<AttendanceSessionRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    s.id AS session_id,
                    ts.id AS timetable_session_id,
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    COALESCE(s.actual_start_time, ts.start_time) AS start_time,
                    COALESCE(s.actual_end_time, ts.end_time) AS end_time,
                    ts.venue,
                    s.status
                FROM sessions s
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                WHERE ts.lecturer_id = ?
                ORDER BY s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapAttendanceSessionRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load attendance sessions: " + e.getMessage(), e);
        }

        return rows;
    }

    /**
     * Loads all attendance sessions.
     * @author methum
     */
    public List<AttendanceSessionRow> findAllAttendanceSessions() {
        List<AttendanceSessionRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    s.id AS session_id,
                    ts.id AS timetable_session_id,
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    COALESCE(s.actual_start_time, ts.start_time) AS start_time,
                    COALESCE(s.actual_end_time, ts.end_time) AS end_time,
                    ts.venue,
                    s.status
                FROM sessions s
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                ORDER BY s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rows.add(mapAttendanceSessionRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all attendance sessions: " + e.getMessage(), e);
        }

        return rows;
    }

    /**
     * Loads editable student attendance rows by session id.
     * @param sessionId session id
     * @author methum
     */
    public List<StudentAttendanceEditRow> findStudentAttendanceRowsBySession(int sessionId) {
        List<StudentAttendanceEditRow> rows = new ArrayList<>();
        String sql = """
                SELECT
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    COALESCE(a.attendance_status, 'ABSENT') AS attendance_status,
                    COALESCE(MAX(CASE WHEN m.approval_status = 'APPROVED' THEN 'APPROVED' ELSE m.approval_status END), '') AS medical_approval_status
                FROM sessions s
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN (
                    SELECT DISTINCT student_reg_no, course_id
                    FROM marks
                ) mk ON mk.course_id = ts.course_id
                INNER JOIN student st ON st.registration_no = mk.student_reg_no
                INNER JOIN users u ON u.id = st.user_id
                LEFT JOIN attendance a
                    ON a.session_id = s.id
                   AND a.student_reg_no = st.registration_no
                LEFT JOIN medical_sessions ms
                    ON ms.session_id = s.id
                LEFT JOIN medicals m
                    ON m.id = ms.medical_id
                   AND m.student_reg_no = st.registration_no
                WHERE s.id = ?
                GROUP BY st.registration_no, u.first_name, u.last_name, a.attendance_status
                ORDER BY st.registration_no
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapStudentAttendanceEditRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load session attendance rows: " + e.getMessage(), e);
        }

        return rows;
    }

    /**
     * Creates an attendance session from a timetable entry for the lecturer flow.
     * @param lecturerId lecturer user id
     * @param courseId course id
     * @param timetableSessionId timetable session id
     * @param sessionDateValue session date
     * @author methum
     */
    public AttendanceSessionRow createSessionFromTimetable(int lecturerId, int courseId, int timetableSessionId, LocalDate sessionDateValue) {
        String validateSql = """
                SELECT ts.id, ts.course_id, ts.session_day, ts.start_time, ts.end_time, ts.venue, ts.session_type
                FROM timetable_sessions ts
                WHERE ts.id = ? AND ts.course_id = ? AND ts.lecturer_id = ?
                """;
        String previousSql = """
                SELECT MAX(session_no) AS max_session_no
                FROM sessions
                WHERE timetable_session_id = ?
                """;
        String insertSql = """
                INSERT INTO sessions (timetable_session_id, session_no, session_date, actual_start_time, actual_end_time, status)
                VALUES (?, ?, ?, ?, ?, 'COMPLETED')
                """;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement validateStmt = conn.prepareStatement(validateSql);
                 PreparedStatement previousStmt = conn.prepareStatement(previousSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                validateStmt.setInt(1, timetableSessionId);
                validateStmt.setInt(2, courseId);
                validateStmt.setInt(3, lecturerId);

                Time startTime;
                Time endTime;

                try (ResultSet validRs = validateStmt.executeQuery()) {
                    if (!validRs.next()) {
                        throw new RuntimeException("Selected timetable session is invalid for this lecturer.");
                    }
                    startTime = validRs.getTime("start_time");
                    endTime = validRs.getTime("end_time");
                }

                int nextSessionNo = resolveNextSessionNumber(previousStmt, timetableSessionId);
                insertCompletedSession(insertStmt, timetableSessionId, nextSessionNo, sessionDateValue, startTime, endTime);

                int sessionId;
                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new RuntimeException("Failed to create attendance session.");
                    }
                    sessionId = keys.getInt(1);
                }

                conn.commit();
                return findAttendanceSessionById(sessionId);
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to create attendance session: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create attendance session: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an attendance session from a timetable entry for the TO flow.
     * @param courseId course id
     * @param timetableSessionId timetable session id
     * @param sessionDateValue session date
     * @author methum
     */
    public AttendanceSessionRow createSessionFromTimetable(int courseId, int timetableSessionId, LocalDate sessionDateValue) {
        String validateSql = """
                SELECT ts.id, ts.course_id, ts.start_time, ts.end_time
                FROM timetable_sessions ts
                WHERE ts.id = ? AND ts.course_id = ?
                """;
        String previousSql = """
                SELECT MAX(session_no) AS max_session_no
                FROM sessions
                WHERE timetable_session_id = ?
                """;
        String insertSql = """
                INSERT INTO sessions (timetable_session_id, session_no, session_date, actual_start_time, actual_end_time, status)
                VALUES (?, ?, ?, ?, ?, 'COMPLETED')
                """;

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement validateStmt = conn.prepareStatement(validateSql);
                 PreparedStatement previousStmt = conn.prepareStatement(previousSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                validateStmt.setInt(1, timetableSessionId);
                validateStmt.setInt(2, courseId);

                Time startTime;
                Time endTime;

                try (ResultSet validRs = validateStmt.executeQuery()) {
                    if (!validRs.next()) {
                        throw new RuntimeException("Selected timetable session is invalid.");
                    }
                    startTime = validRs.getTime("start_time");
                    endTime = validRs.getTime("end_time");
                }

                int nextSessionNo = resolveNextSessionNumber(previousStmt, timetableSessionId);
                insertCompletedSession(insertStmt, timetableSessionId, nextSessionNo, sessionDateValue, startTime, endTime);

                int sessionId;
                try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new RuntimeException("Failed to create attendance session.");
                    }
                    sessionId = keys.getInt(1);
                }

                conn.commit();
                return findAttendanceSessionById(sessionId);
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to create attendance session: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create attendance session: " + e.getMessage(), e);
        }
    }

    /**
     * Loads one attendance session by id.
     * @param sessionId session id
     * @author methum
     */
    public AttendanceSessionRow findAttendanceSessionById(int sessionId) {
        String sql = """
                SELECT
                    s.id AS session_id,
                    ts.id AS timetable_session_id,
                    c.id AS course_id,
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    ts.session_day,
                    COALESCE(s.actual_start_time, ts.start_time) AS start_time,
                    COALESCE(s.actual_end_time, ts.end_time) AS end_time,
                    ts.venue,
                    s.status
                FROM sessions s
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                WHERE s.id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAttendanceSessionRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load attendance session: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Maps an attendance session row from the result set.
     * @param rs result set
     * @author methum
     */
    private AttendanceSessionRow mapAttendanceSessionRow(ResultSet rs) throws SQLException {
        AttendanceSessionRow row = new AttendanceSessionRow();
        row.setSessionId(rs.getInt("session_id"));
        row.setTimetableSessionId(rs.getInt("timetable_session_id"));
        row.setCourseId(rs.getInt("course_id"));
        row.setCourseCode(rs.getString("course_code"));
        row.setCourseName(rs.getString("course_name"));
        row.setSessionType(rs.getString("session_type"));
        row.setSessionNo(rs.getInt("session_no"));
        row.setSessionDate(String.valueOf(rs.getDate("session_date")));
        row.setSessionDay(rs.getString("session_day"));
        row.setTimeRange(formatTimeRange(rs.getString("start_time"), rs.getString("end_time")));
        row.setVenue(rs.getString("venue"));
        row.setSessionStatus(valueOrEmpty(rs.getString("status")));
        return row;
    }

    /**
     * Saves per-student attendance statuses for a session.
     * @param sessionId session id
     * @param markedBy marker user id
     * @param updates status updates
     * @author methum
     */
    public void saveSessionAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates) {
        String existingSql = "SELECT id FROM attendance WHERE student_reg_no = ? AND session_id = ?";
        String insertSql = """
                INSERT INTO attendance (student_reg_no, session_id, attendance_status, marked_by, remarks)
                VALUES (?, ?, ?, ?, NULL)
                """;
        String updateSql = """
                UPDATE attendance
                SET attendance_status = ?, marked_by = ?, marked_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement existingStmt = conn.prepareStatement(existingSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                for (StudentAttendanceUpdate update : updates) {
                    existingStmt.setString(1, update.getRegistrationNo());
                    existingStmt.setInt(2, sessionId);
                    try (ResultSet rs = existingStmt.executeQuery()) {
                        if (rs.next()) {
                            updateStmt.setString(1, update.getAttendanceStatus());
                            updateStmt.setInt(2, markedBy);
                            updateStmt.setInt(3, rs.getInt("id"));
                            updateStmt.addBatch();
                        } else {
                            insertStmt.setString(1, update.getRegistrationNo());
                            insertStmt.setInt(2, sessionId);
                            insertStmt.setString(3, update.getAttendanceStatus());
                            insertStmt.setInt(4, markedBy);
                            insertStmt.addBatch();
                        }
                    }
                }
                insertStmt.executeBatch();
                updateStmt.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to save attendance: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save attendance: " + e.getMessage(), e);
        }
    }

    /**
     * Maps one editable student attendance row from the result set.
     * @param rs result set
     * @author methum
     */
    private StudentAttendanceEditRow mapStudentAttendanceEditRow(ResultSet rs) throws SQLException {
        StudentAttendanceEditRow row = new StudentAttendanceEditRow();
        row.setRegistrationNo(rs.getString("registration_no"));
        row.setStudentName(rs.getString("student_name"));
        row.setAttendanceStatus(valueOrEmpty(rs.getString("attendance_status")));
        row.setMedicalApprovalStatus(valueOrEmpty(rs.getString("medical_approval_status")));
        return row;
    }

    /**
     * Resolves the next session number for a timetable session.
     * @param previousStmt prepared statement for the previous-session lookup
     * @param timetableSessionId timetable session id
     * @author methum
     */
    private int resolveNextSessionNumber(PreparedStatement previousStmt, int timetableSessionId) throws SQLException {
        previousStmt.setInt(1, timetableSessionId);
        try (ResultSet prevRs = previousStmt.executeQuery()) {
            if (prevRs.next()) {
                return prevRs.getInt("max_session_no") + 1;
            }
        }
        return 1;
    }

    /**
     * Inserts a completed session row using the selected timetable schedule.
     * @param insertStmt prepared statement for session insertion
     * @param timetableSessionId timetable session id
     * @param sessionNo next session number
     * @param sessionDateValue session date
     * @param startTime actual start time
     * @param endTime actual end time
     * @author methum
     */
    private void insertCompletedSession(
            PreparedStatement insertStmt,
            int timetableSessionId,
            int sessionNo,
            LocalDate sessionDateValue,
            Time startTime,
            Time endTime
    ) throws SQLException {
        insertStmt.setInt(1, timetableSessionId);
        insertStmt.setInt(2, sessionNo);
        insertStmt.setDate(3, Date.valueOf(sessionDateValue));
        insertStmt.setTime(4, startTime);
        insertStmt.setTime(5, endTime);
        insertStmt.executeUpdate();
    }

    /**
     * load student attendance rows for student dashboard
     * @param studentUserId student user id
     * @author poornika
     */
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
                    COALESCE(MAX(CASE WHEN m.approval_status = 'APPROVED' THEN 'APPROVED' ELSE m.approval_status END), '') AS medical_approval_status,
                    COALESCE(a.remarks, '') AS attendance_remarks
                FROM student st
                INNER JOIN marks mk ON mk.student_reg_no = st.registration_no
                INNER JOIN courses c ON c.id = mk.course_id
                INNER JOIN timetable_sessions ts ON ts.course_id = c.id
                INNER JOIN sessions s ON s.timetable_session_id = ts.id
                LEFT JOIN attendance a
                    ON a.session_id = s.id
                   AND a.student_reg_no = st.registration_no
                LEFT JOIN medical_sessions ms
                    ON ms.session_id = s.id
                LEFT JOIN medicals m
                    ON m.id = ms.medical_id
                   AND m.student_reg_no = st.registration_no
                WHERE st.user_id = ?
                GROUP BY c.course_code, c.course_name, ts.session_type, s.session_no, s.session_date,
                         ts.session_day, start_time, end_time, ts.venue, s.status, a.attendance_status, a.remarks
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

    /**
     * load student medical submissions with linked session details
     * @param studentUserId student user id
     * @author poornika
     */
    public List<StudentMedicalRow> findStudentMedicalRows(int studentUserId) {
        Map<Integer, StudentMedicalRow> groupedRows = new LinkedHashMap<>();
        String sql = """
                SELECT
                    m.id,
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
                INNER JOIN medical_sessions ms ON ms.medical_id = m.id
                INNER JOIN sessions s ON s.id = ms.session_id
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                WHERE st.user_id = ?
                ORDER BY s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int medicalId = rs.getInt("id");
                    StudentMedicalRow row = groupedRows.computeIfAbsent(medicalId, ignored -> {
                        StudentMedicalRow created = new StudentMedicalRow();
                        created.setMedicalId(medicalId);
                        created.setSubmittedDate(String.valueOf(rsGetDate(rs, "submitted_date")));
                        created.setApprovalStatus(valueOrEmpty(rsGetString(rs, "approval_status")));
                        created.setApprovedAt(rsGetTimestamp(rs, "approved_at"));
                        created.setRemarks(valueOrEmpty(rsGetString(rs, "remarks")));
                        created.setMedicalDocument(valueOrEmpty(rsGetString(rs, "medical_document")));
                        return created;
                    });

                    MedicalSessionDetail detail = new MedicalSessionDetail();
                    detail.setCourseCode(rs.getString("course_code"));
                    detail.setCourseName(rs.getString("course_name"));
                    detail.setSessionType(rs.getString("session_type"));
                    detail.setSessionNo(rs.getInt("session_no"));
                    detail.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    row.getSessionDetails().add(detail);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load student medical rows: " + e.getMessage(), e);
        }

        return new ArrayList<>(groupedRows.values());
    }

    /**
     * resolve student registration number by user id
     * @param studentUserId student user id
     * @author poornika
     */
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

    /**
     * load absent sessions available for medical submission range
     * @param studentUserId student user id
     * @param startDate range start date
     * @param endDate range end date
     * @author poornika
     */
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
                LEFT JOIN medical_sessions ms
                    ON ms.session_id = s.id
                LEFT JOIN medicals m
                    ON m.id = ms.medical_id
                   AND m.student_reg_no = st.registration_no
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

    /**
     * save one medical submission and link selected absent sessions
     * @param studentUserId student user id
     * @param sessionIds selected session ids
     * @param medicalDocumentPath stored document path
     * @param submittedDate submitted date
     * @author poornika
     */
    public void saveStudentMedicalSubmissions(int studentUserId, List<Integer> sessionIds, String medicalDocumentPath, Date submittedDate) {
        String registrationNo = findStudentRegistrationNoByUserId(studentUserId);
        if (registrationNo == null || registrationNo.trim().isEmpty()) {
            throw new RuntimeException("Student registration could not be found.");
        }

        String validationSql = """
                SELECT COUNT(*)
                FROM attendance a
                INNER JOIN student st ON st.registration_no = a.student_reg_no
                LEFT JOIN medical_sessions ms
                    ON ms.session_id = a.session_id
                LEFT JOIN medicals m
                    ON m.id = ms.medical_id
                   AND m.student_reg_no = st.registration_no
                WHERE st.user_id = ?
                  AND a.session_id = ?
                  AND a.attendance_status = 'ABSENT'
                  AND m.id IS NULL
                """;

        String insertMedicalSql = """
                INSERT INTO medicals (student_reg_no, medical_document, submitted_date, approval_status, remarks)
                VALUES (?, ?, ?, 'PENDING', '')
                """;

        String insertMedicalSessionSql = """
                INSERT INTO medical_sessions (medical_id, session_id)
                VALUES (?, ?)
                """;

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement validateStmt = conn.prepareStatement(validationSql);
                 PreparedStatement insertMedicalStmt = conn.prepareStatement(insertMedicalSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement insertMedicalSessionStmt = conn.prepareStatement(insertMedicalSessionSql)) {
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
                }

                insertMedicalStmt.setString(1, registrationNo);
                insertMedicalStmt.setString(2, medicalDocumentPath);
                insertMedicalStmt.setDate(3, submittedDate);
                insertMedicalStmt.executeUpdate();

                int medicalId;
                try (ResultSet keys = insertMedicalStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new RuntimeException("Failed to create medical submission.");
                    }
                    medicalId = keys.getInt(1);
                }

                for (Integer sessionId : sessionIds) {
                    insertMedicalSessionStmt.setInt(1, medicalId);
                    insertMedicalSessionStmt.setInt(2, sessionId);
                    insertMedicalSessionStmt.addBatch();
                }

                insertMedicalSessionStmt.executeBatch();
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

    /**
     * count currently pending medical submissions
     * @author methum
     */
    public int countPendingMedicalSubmissions() {
        String sql = "SELECT COUNT(*) FROM medicals WHERE approval_status = 'PENDING'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count pending medical submissions: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * load medical rows by approval status for TO review
     * @param status medical status filter
     * @author methum
     */
    public List<MedicalApprovalRow> findMedicalRowsByStatus(String status) {
        Map<Integer, MedicalApprovalRow> groupedRows = new LinkedHashMap<>();
        String sql = """
                SELECT
                    m.id,
                    st.registration_no,
                    CONCAT(u.first_name, ' ', u.last_name) AS student_name,
                    c.course_code,
                    c.course_name,
                    ts.session_type,
                    s.session_no,
                    s.session_date,
                    m.submitted_date,
                    m.approval_status,
                    m.approved_at,
                    m.medical_document
                FROM medicals m
                INNER JOIN student st ON st.registration_no = m.student_reg_no
                INNER JOIN users u ON u.id = st.user_id
                INNER JOIN medical_sessions ms ON ms.medical_id = m.id
                INNER JOIN sessions s ON s.id = ms.session_id
                INNER JOIN timetable_sessions ts ON ts.id = s.timetable_session_id
                INNER JOIN courses c ON c.id = ts.course_id
                WHERE m.approval_status = ?
                ORDER BY m.submitted_date DESC, s.session_date DESC, c.course_code, s.session_no DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int medicalId = rs.getInt("id");
                    MedicalApprovalRow row = groupedRows.computeIfAbsent(medicalId, ignored -> {
                        MedicalApprovalRow created = new MedicalApprovalRow();
                        created.setMedicalId(medicalId);
                        created.setRegistrationNo(rsGetString(rs, "registration_no"));
                        created.setStudentName(rsGetString(rs, "student_name"));
                        created.setSubmittedDate(String.valueOf(rsGetDate(rs, "submitted_date")));
                        created.setApprovalStatus(valueOrEmpty(rsGetString(rs, "approval_status")));
                        created.setApprovedAt(rsGetTimestamp(rs, "approved_at"));
                        created.setMedicalDocument(valueOrEmpty(rsGetString(rs, "medical_document")));
                        return created;
                    });

                    MedicalSessionDetail detail = new MedicalSessionDetail();
                    detail.setCourseCode(rs.getString("course_code"));
                    detail.setCourseName(rs.getString("course_name"));
                    detail.setSessionType(rs.getString("session_type"));
                    detail.setSessionNo(rs.getInt("session_no"));
                    detail.setSessionDate(String.valueOf(rs.getDate("session_date")));
                    row.getSessionDetails().add(detail);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medical rows: " + e.getMessage(), e);
        }

        return new ArrayList<>(groupedRows.values());
    }

    /**
     * safe string read helper for lambda mapping blocks
     * @param rs result set
     * @param column column name
     * @author poornika
     */
    private String rsGetString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * safe date read helper for lambda mapping blocks
     * @param rs result set
     * @param column column name
     * @author poornika
     */
    private Date rsGetDate(ResultSet rs, String column) {
        try {
            return rs.getDate(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * safe timestamp string read helper
     * @param rs result set
     * @param column column name
     * @author poornika
     */
    private String rsGetTimestamp(ResultSet rs, String column) {
        try {
            Timestamp timestamp = rs.getTimestamp(column);
            return timestamp == null ? "" : String.valueOf(timestamp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * approve medical submission and upgrade linked attendance rows
     * @param medicalId medical id
     * @param approvedBy approver user id
     * @author methum
     */
    public void approveMedical(int medicalId, int approvedBy) {
        String approveMedicalSql = """
                UPDATE medicals
                SET approval_status = 'APPROVED',
                    approved_by = ?,
                    approved_at = CURRENT_TIMESTAMP,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND approval_status = 'PENDING'
                """;
        String updateAttendanceSql = """
                UPDATE attendance a
                INNER JOIN medical_sessions ms ON ms.session_id = a.session_id
                INNER JOIN medicals m ON m.id = ms.medical_id
                SET a.attendance_status = 'MEDICAL',
                    a.marked_by = ?,
                    a.marked_at = CURRENT_TIMESTAMP
                WHERE m.id = ?
                  AND m.approval_status = 'APPROVED'
                  AND a.student_reg_no = m.student_reg_no
                  AND a.attendance_status = 'ABSENT'
                """;

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement approveMedicalStmt = conn.prepareStatement(approveMedicalSql);
                 PreparedStatement updateAttendanceStmt = conn.prepareStatement(updateAttendanceSql)) {
                approveMedicalStmt.setInt(1, approvedBy);
                approveMedicalStmt.setInt(2, medicalId);
                int updated = approveMedicalStmt.executeUpdate();
                if (updated == 0) {
                    throw new RuntimeException("Medical record could not be approved.");
                }

                updateAttendanceStmt.setInt(1, approvedBy);
                updateAttendanceStmt.setInt(2, medicalId);
                updateAttendanceStmt.executeUpdate();

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException("Failed to approve medical record: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to approve medical record: " + e.getMessage(), e);
        }
    }
}
