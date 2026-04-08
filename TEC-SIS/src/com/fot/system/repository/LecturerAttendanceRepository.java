package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.AttendanceTableRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerAttendanceRepository {

    private final Connection conn;

    public LecturerAttendanceRepository() {
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
}
