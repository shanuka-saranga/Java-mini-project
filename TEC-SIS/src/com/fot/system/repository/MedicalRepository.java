package com.fot.system.repository;

import com.fot.system.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MedicalRepository - Data Access Layer for Medicals and MedicalParticipation tables.
 * Demonstrates: Database Handling, Encapsulation
 */
public class MedicalRepository {

    private final Connection conn;

    public MedicalRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Save a new medical record and its related lecture participations.
     */
    public boolean save(Medical medical, List<Integer> lectureIds) {
        String sqlMedical = "INSERT INTO Medicals (StudentRegNo, StartDate, EndDate, DocumentPath, ApprovalStatus) " +
                            "VALUES (?, ?, ?, ?, 'Pending')";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlMedical, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, medical.getStudentRegNo());
                stmt.setDate(2, new Date(medical.getStartDate().getTime()));
                stmt.setDate(3, new Date(medical.getEndDate().getTime()));
                stmt.setString(4, medical.getDocumentPath());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int newId = keys.getInt(1);
                        medical.setMedicalId(newId);

                        // Save MedicalParticipation rows
                        if (lectureIds != null && !lectureIds.isEmpty()) {
                            saveMedicalParticipation(newId, lectureIds);
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving medical: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void saveMedicalParticipation(int medicalId, List<Integer> lectureIds) throws SQLException {
        String sql = "INSERT INTO MedicalParticipation (MedicalID, LectureID) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int lectureId : lectureIds) {
                stmt.setInt(1, medicalId);
                stmt.setInt(2, lectureId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Update medical approval status (Approved / Rejected).
     */
    public boolean updateApprovalStatus(int medicalId, String status) {
        String sql = "UPDATE Medicals SET ApprovalStatus = ? WHERE MedicalID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, medicalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medical status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all medicals for a specific student.
     */
    public List<Medical> findByStudent(String regNo) {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, u.first_name, u.last_name " +
                     "FROM Medicals m " +
                     "JOIN student s ON m.StudentRegNo = s.registration_no " +
                     "JOIN users u ON s.user_id = u.id " +
                     "WHERE m.StudentRegNo = ? " +
                     "ORDER BY m.StartDate DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, regNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Medical m = mapRow(rs);
                    m.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicals by student: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get all pending medicals (for TO to review).
     */
    public List<Medical> findAllPending() {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, u.first_name, u.last_name " +
                     "FROM Medicals m " +
                     "JOIN student s ON m.StudentRegNo = s.registration_no " +
                     "JOIN users u ON s.user_id = u.id " +
                     "WHERE m.ApprovalStatus = 'Pending' " +
                     "ORDER BY m.SubmittedDate DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medical m = mapRow(rs);
                m.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending medicals: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get all medicals (all statuses).
     */
    public List<Medical> findAll() {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT m.*, u.first_name, u.last_name " +
                     "FROM Medicals m " +
                     "JOIN student s ON m.StudentRegNo = s.registration_no " +
                     "JOIN users u ON s.user_id = u.id " +
                     "ORDER BY m.SubmittedDate DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medical m = mapRow(rs);
                m.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all medicals: " + e.getMessage());
        }
        return list;
    }

    private Medical mapRow(ResultSet rs) throws SQLException {
        Medical m = new Medical();
        m.setMedicalId(rs.getInt("MedicalID"));
        m.setStudentRegNo(rs.getString("StudentRegNo"));
        m.setStartDate(rs.getDate("StartDate"));
        m.setEndDate(rs.getDate("EndDate"));
        m.setSubmittedDate(rs.getTimestamp("SubmittedDate"));
        m.setDocumentPath(rs.getString("DocumentPath"));
        m.setApprovalStatus(rs.getString("ApprovalStatus"));
        return m;
    }
}
