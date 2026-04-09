package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.TimetableSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {
    private final Connection conn;

    public TimetableRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<TimetableSession> getAllTimetableSessions() {
        List<TimetableSession> sessions = new ArrayList<>();
        String sql = """
            SELECT ts.*, c.course_code, c.course_name
            FROM timetable_sessions ts
            JOIN courses c ON ts.course_id = c.id
            ORDER BY
                FIELD(UPPER(ts.session_day), 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'),
                ts.start_time,
                c.course_code
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public List<TimetableSession> getTimetableByLecturer(int lecturerId) {
        List<TimetableSession> sessions = new ArrayList<>();
        String sql = """
            SELECT ts.*, c.course_code, c.course_name 
            FROM timetable_sessions ts
            JOIN courses c ON ts.course_id = c.id
            WHERE ts.lecturer_id = ?
            ORDER BY ts.session_day, ts.start_time
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, lecturerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapSession(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    private TimetableSession mapSession(ResultSet rs) throws SQLException {
        TimetableSession session = new TimetableSession();
        session.setDay(rs.getString("session_day"));
        session.setCourseCode(rs.getString("course_code"));
        session.setCourseName(rs.getString("course_name"));
        session.setStartTime(rs.getTime("start_time") == null ? null : rs.getTime("start_time").toString());
        session.setEndTime(rs.getTime("end_time") == null ? null : rs.getTime("end_time").toString());
        session.setVenue(rs.getString("venue"));
        session.setSessionType(rs.getString("session_type"));
        return session;
    }
}
