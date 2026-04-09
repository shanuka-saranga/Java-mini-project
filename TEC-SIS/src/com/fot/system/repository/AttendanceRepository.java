package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.AttendanceTableRow;
import com.fot.system.model.StudentAttendance;

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
