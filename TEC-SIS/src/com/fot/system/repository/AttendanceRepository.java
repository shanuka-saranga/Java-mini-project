package com.fot.system.repository;

import com.fot.system.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceRepository - Data Access Layer for Attendance table.
 * Demonstrates: Database Handling, Encapsulation
 */
public class AttendanceRepository {

    private final Connection conn;

    public AttendanceRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Add a new attendance record.
     */
    public boolean save(Attendance attendance) {
        String sql = "INSERT INTO Attendance (RegNo, LectureID, SessionDate, Status, RecordedBy) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, attendance.getRegNo());
            stmt.setInt(2, attendance.getLectureId());
            stmt.setDate(3, new Date(attendance.getSessionDate().getTime()));
            stmt.setString(4, attendance.getStatus());
            stmt.setInt(5, attendance.getRecordedBy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing attendance record.
     */
    public boolean update(Attendance attendance) {
        String sql = "UPDATE Attendance SET Status = ? WHERE AttendanceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, attendance.getStatus());
            stmt.setInt(2, attendance.getAttendanceId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an attendance record by ID.
     */
    public boolean delete(int attendanceId) {
        String sql = "DELETE FROM Attendance WHERE AttendanceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all attendance records for a specific student.
     */
    public List<Attendance> findByStudent(String regNo) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_code, c.course_name, l.session_type " +
                     "FROM Attendance a " +
                     "JOIN lecture l ON a.LectureID = l.lecture_id " +
                     "JOIN course c ON l.course_id = c.course_id " +
                     "WHERE a.RegNo = ? " +
                     "ORDER BY a.SessionDate DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = mapRow(rs);
                    a.setCourseCode(rs.getString("course_code"));
                    a.setCourseName(rs.getString("course_name"));
                    a.setSessionType(rs.getString("session_type"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance by student: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get all attendance for a specific lecture session.
     */
    public List<Attendance> findByLecture(int lectureId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.first_name, u.last_name " +
                     "FROM Attendance a " +
                     "JOIN users u ON a.RegNo = (SELECT registration_no FROM student WHERE user_id = u.id) " +
                     "WHERE a.LectureID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lectureId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = mapRow(rs);
                    a.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance by lecture: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get attendance summary for a student per course.
     * Returns: regNo, courseCode, courseName, sessionType, totalSessions, presentCount
     */
    public List<Object[]> getAttendanceSummaryByStudent(String regNo) {
        List<Object[]> summary = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, l.session_type, " +
                     "COUNT(*) AS total_sessions, " +
                     "SUM(CASE WHEN a.Status = 'Present' THEN 1 ELSE 0 END) AS present_count " +
                     "FROM Attendance a " +
                     "JOIN lecture l ON a.LectureID = l.lecture_id " +
                     "JOIN course c ON l.course_id = c.course_id " +
                     "WHERE a.RegNo = ? " +
                     "GROUP BY c.course_code, c.course_name, l.session_type " +
                     "ORDER BY c.course_code, l.session_type";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    summary.add(new Object[]{
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("session_type"),
                            rs.getInt("total_sessions"),
                            rs.getInt("present_count")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting attendance summary: " + e.getMessage());
        }
        return summary;
    }

    /**
     * Get attendance summary for all students (whole batch) per course.
     * Returns: regNo, courseCode, sessionType, totalSessions, presentCount
     */
    public List<Object[]> getAttendanceSummaryAllStudents() {
        List<Object[]> summary = new ArrayList<>();
        String sql = "SELECT a.RegNo, u.first_name, u.last_name, " +
                     "c.course_code, c.course_name, l.session_type, " +
                     "COUNT(*) AS total_sessions, " +
                     "SUM(CASE WHEN a.Status = 'Present' THEN 1 ELSE 0 END) AS present_count " +
                     "FROM Attendance a " +
                     "JOIN lecture l ON a.LectureID = l.lecture_id " +
                     "JOIN course c ON l.course_id = c.course_id " +
                     "JOIN student s ON a.RegNo = s.registration_no " +
                     "JOIN users u ON s.user_id = u.id " +
                     "GROUP BY a.RegNo, c.course_code, c.course_name, l.session_type " +
                     "ORDER BY a.RegNo, c.course_code";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                summary.add(new Object[]{
                        rs.getString("RegNo"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("session_type"),
                        rs.getInt("total_sessions"),
                        rs.getInt("present_count")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting batch attendance summary: " + e.getMessage());
        }
        return summary;
    }

    /**
     * Check if a record already exists for a student + lecture.
     */
    public boolean exists(String regNo, int lectureId) {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE RegNo = ? AND LectureID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            stmt.setInt(2, lectureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking attendance existence: " + e.getMessage());
        }
        return false;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("AttendanceID"));
        a.setRegNo(rs.getString("RegNo"));
        a.setLectureId(rs.getInt("LectureID"));
        a.setSessionDate(rs.getDate("SessionDate"));
        a.setStatus(rs.getString("Status"));
        a.setRecordedBy(rs.getInt("RecordedBy"));
        return a;
    }
}
