package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

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
            SELECT ts.*, c.course_code, c.course_name,
                   CONCAT(u.first_name, ' ', u.last_name) AS lecturer_name
            FROM timetable_sessions ts
            JOIN courses c ON ts.course_id = c.id
            LEFT JOIN users u ON u.id = ts.lecturer_id
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
            SELECT ts.*, c.course_code, c.course_name,
                   CONCAT(u.first_name, ' ', u.last_name) AS lecturer_name
            FROM timetable_sessions ts
            JOIN courses c ON ts.course_id = c.id
            LEFT JOIN users u ON u.id = ts.lecturer_id
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

    public TimetableSession findById(int sessionId) {
        String sql = """
            SELECT ts.*, c.course_code, c.course_name,
                   CONCAT(u.first_name, ' ', u.last_name) AS lecturer_name
            FROM timetable_sessions ts
            JOIN courses c ON ts.course_id = c.id
            LEFT JOIN users u ON u.id = ts.lecturer_id
            WHERE ts.id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSession(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load timetable session: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean save(TimetableSession session) {
        String sql = """
            INSERT INTO timetable_sessions (course_id, session_type, session_day, start_time, end_time, venue, lecturer_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindSession(stmt, session);
            if (stmt.executeUpdate() <= 0) {
                return false;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    session.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save timetable session: " + e.getMessage(), e);
        }
    }

    public boolean update(TimetableSession session) {
        String sql = """
            UPDATE timetable_sessions
            SET course_id = ?, session_type = ?, session_day = ?, start_time = ?, end_time = ?, venue = ?, lecturer_id = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindSession(stmt, session);
            stmt.setInt(8, session.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update timetable session: " + e.getMessage(), e);
        }
    }

    public boolean deleteById(int sessionId) {
        String sql = "DELETE FROM timetable_sessions WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete timetable session: " + e.getMessage(), e);
        }
    }

    private TimetableSession mapSession(ResultSet rs) throws SQLException {
        TimetableSession session = new TimetableSession();
        session.setId(rs.getInt("id"));
        session.setCourseId(rs.getInt("course_id"));
        int lecturerId = rs.getInt("lecturer_id");
        if (!rs.wasNull()) {
            session.setLecturerId(lecturerId);
        }
        session.setDay(rs.getString("session_day"));
        session.setCourseCode(rs.getString("course_code"));
        session.setCourseName(rs.getString("course_name"));
        session.setLecturerName(rs.getString("lecturer_name"));
        session.setStartTime(rs.getTime("start_time") == null ? null : rs.getTime("start_time").toString());
        session.setEndTime(rs.getTime("end_time") == null ? null : rs.getTime("end_time").toString());
        session.setVenue(rs.getString("venue"));
        session.setSessionType(rs.getString("session_type"));
        return session;
    }

    private void bindSession(PreparedStatement stmt, TimetableSession session) throws SQLException {
        stmt.setInt(1, session.getCourseId());
        stmt.setString(2, session.getSessionType());
        stmt.setString(3, session.getDay());
        stmt.setTime(4, Time.valueOf(session.getStartTime()));
        stmt.setTime(5, Time.valueOf(session.getEndTime()));
        stmt.setString(6, session.getVenue());
        if (session.getLecturerId() == null) {
            stmt.setNull(7, Types.INTEGER);
        } else {
            stmt.setInt(7, session.getLecturerId());
        }
    }
}
