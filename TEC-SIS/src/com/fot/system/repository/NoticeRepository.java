package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * handle notice persistence and lookup queries
 * @author janith
 */
public class NoticeRepository {

    private final Connection conn;

    /**
     * initialize notice repository and database connection
     * @author janith
     */
    public NoticeRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * find all notices with creator name
     * @author janith
     */
    public List<Notice> findAll() {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT n.*, CONCAT(u.first_name, ' ', u.last_name) AS created_by_name " +
                "FROM notices n INNER JOIN users u ON u.id = n.created_by ORDER BY n.published_date DESC, n.id DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notices.add(mapNotice(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load notices: " + e.getMessage(), e);
        }

        return notices;
    }

    /**
     * find notice by id
     * @param noticeId notice id
     * @author janith
     */
    public Notice findById(int noticeId) {
        String sql = "SELECT n.*, CONCAT(u.first_name, ' ', u.last_name) AS created_by_name " +
                "FROM notices n INNER JOIN users u ON u.id = n.created_by WHERE n.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noticeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapNotice(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load notice: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * save new notice record
     * @param notice notice entity
     * @author janith
     */
    public boolean save(Notice notice) {
        String sql = "INSERT INTO notices (title, content, audience, priority, status, published_date, expiry_date, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindNotice(stmt, notice);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    notice.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save notice: " + e.getMessage(), e);
        }
    }

    /**
     * update existing notice record
     * @param notice notice entity
     * @author janith
     */
    public boolean update(Notice notice) {
        String sql = "UPDATE notices SET title = ?, content = ?, audience = ?, priority = ?, status = ?, published_date = ?, expiry_date = ?, created_by = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            bindNotice(stmt, notice);
            stmt.setInt(9, notice.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update notice: " + e.getMessage(), e);
        }
    }

    /**
     * delete notice by id
     * @param noticeId notice id
     * @author janith
     */
    public boolean deleteById(int noticeId) {
        String sql = "DELETE FROM notices WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noticeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete notice: " + e.getMessage(), e);
        }
    }

    /**
     * count active and currently visible notices
     * @author janith
     */
    public int countActive() {
        String sql = "SELECT COUNT(*) FROM notices WHERE status = 'ACTIVE' AND published_date <= CURDATE() " +
                "AND (expiry_date IS NULL OR expiry_date >= CURDATE())";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count notices: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * count notices visible for a specific role
     * @param role user role
     * @author janith
     */
    public int countVisibleByRole(String role) {
        String normalizedRole = normalizeRole(role);
        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(normalizedRole)) {
            return countActive();
        }

        String sql = "SELECT COUNT(*) FROM notices WHERE status = 'ACTIVE' AND published_date <= CURDATE() " +
                "AND (expiry_date IS NULL OR expiry_date >= CURDATE()) AND (audience = 'ALL' OR audience = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizedRole);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count visible notices: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * find recent visible notices for a role with limit
     * @param role user role
     * @param limit max items
     * @author janith
     */
    public List<Notice> findRecentVisibleByRole(String role, int limit) {
        List<Notice> notices = new ArrayList<>();
        String normalizedRole = normalizeRole(role);
        boolean adminRole = AppConfig.ROLE_ADMIN.equalsIgnoreCase(normalizedRole);

        String sql = "SELECT n.*, CONCAT(u.first_name, ' ', u.last_name) AS created_by_name " +
                "FROM notices n INNER JOIN users u ON u.id = n.created_by " +
                "WHERE n.status = 'ACTIVE' AND n.published_date <= CURDATE() " +
                "AND (n.expiry_date IS NULL OR n.expiry_date >= CURDATE()) " +
                (adminRole ? "" : "AND (n.audience = 'ALL' OR n.audience = ?) ") +
                "ORDER BY FIELD(n.priority, 'HIGH', 'MEDIUM', 'LOW'), n.published_date DESC, n.id DESC LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            if (!adminRole) {
                stmt.setString(index++, normalizedRole);
            }
            stmt.setInt(index, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notices.add(mapNotice(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load visible notices: " + e.getMessage(), e);
        }

        return notices;
    }

    /**
     * bind notice entity fields into prepared statement
     * @param stmt prepared statement
     * @param notice notice entity
     * @author janith
     */
    private void bindNotice(PreparedStatement stmt, Notice notice) throws SQLException {
        stmt.setString(1, notice.getTitle());
        stmt.setString(2, notice.getContent());
        stmt.setString(3, notice.getAudience());
        stmt.setString(4, notice.getPriority());
        stmt.setString(5, notice.getStatus());
        stmt.setDate(6, new java.sql.Date(notice.getPublishedDate().getTime()));
        if (notice.getExpiryDate() == null) {
            stmt.setNull(7, java.sql.Types.DATE);
        } else {
            stmt.setDate(7, new java.sql.Date(notice.getExpiryDate().getTime()));
        }
        stmt.setInt(8, notice.getCreatedBy());
    }

    /**
     * map query result row into notice entity
     * @param rs result set row
     * @author janith
     */
    private Notice mapNotice(ResultSet rs) throws SQLException {
        Notice notice = new Notice();
        notice.setId(rs.getInt("id"));
        notice.setTitle(rs.getString("title"));
        notice.setContent(rs.getString("content"));
        notice.setAudience(rs.getString("audience"));
        notice.setPriority(rs.getString("priority"));
        notice.setStatus(rs.getString("status"));
        notice.setPublishedDate(rs.getDate("published_date"));
        notice.setExpiryDate(rs.getDate("expiry_date"));
        notice.setCreatedBy(rs.getInt("created_by"));
        notice.setCreatedByName(rs.getString("created_by_name"));
        return notice;
    }

    /**
     * normalize role text for query comparisons
     * @param role role value
     * @author janith
     */
    private String normalizeRole(String role) {
        return role == null ? "" : role.trim().toUpperCase();
    }
}
