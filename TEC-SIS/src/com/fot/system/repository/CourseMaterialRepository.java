package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialRepository {

    private final Connection conn;

    /**
     * initialize repository with shared db connection
     * @author poornika
     */
    public CourseMaterialRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * find active materials by course id
     * @param courseId course id
     * @author poornika
     */
    public List<CourseMaterial> findByCourseId(int courseId) {
        List<CourseMaterial> materials = new ArrayList<>();
        String sql = "SELECT cm.id, cm.course_id, cm.title, cm.description, cm.file_path, cm.file_type, " +
                "cm.uploaded_by, cm.uploaded_at, cm.status, CONCAT(u.first_name, ' ', u.last_name) AS uploaded_by_name " +
                "FROM course_materials cm " +
                "INNER JOIN users u ON u.id = cm.uploaded_by " +
                "WHERE cm.course_id = ? AND cm.status = 'ACTIVE' " +
                "ORDER BY cm.uploaded_at DESC, cm.id DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapMaterial(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load course materials: " + e.getMessage(), e);
        }

        return materials;
    }

    /**
     * persist new material row and assign generated id
     * @param material material entity
     * @author poornika
     */
    public boolean save(CourseMaterial material) {
        String sql = "INSERT INTO course_materials (course_id, title, description, file_path, file_type, uploaded_by, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, material.getCourseId());
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getDescription());
            stmt.setString(4, material.getFilePath());
            stmt.setString(5, material.getFileType());
            stmt.setInt(6, material.getUploadedBy());
            stmt.setString(7, material.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    material.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save course material: " + e.getMessage(), e);
        }
    }

    /**
     * find single material by id
     * @param materialId material id
     * @author poornika
     */
    public CourseMaterial findById(int materialId) {
        String sql = "SELECT cm.id, cm.course_id, cm.title, cm.description, cm.file_path, cm.file_type, " +
                "cm.uploaded_by, cm.uploaded_at, cm.status, CONCAT(u.first_name, ' ', u.last_name) AS uploaded_by_name " +
                "FROM course_materials cm " +
                "INNER JOIN users u ON u.id = cm.uploaded_by " +
                "WHERE cm.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMaterial(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load course material: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * update editable material fields
     * @param material material entity
     * @author poornika
     */
    public boolean update(CourseMaterial material) {
        String sql = "UPDATE course_materials SET title = ?, description = ?, file_path = ?, file_type = ?, " +
                "uploaded_by = ?, uploaded_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setString(3, material.getFilePath());
            stmt.setString(4, material.getFileType());
            stmt.setInt(5, material.getUploadedBy());
            stmt.setInt(6, material.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update course material: " + e.getMessage(), e);
        }
    }

    /**
     * soft-delete material by status update
     * @param materialId material id
     * @author poornika
     */
    public boolean archive(int materialId) {
        String sql = "UPDATE course_materials SET status = 'ARCHIVED' WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to archive course material: " + e.getMessage(), e);
        }
    }

    /**
     * map result set row into entity
     * @param rs query result set
     * @author poornika
     */
    private CourseMaterial mapMaterial(ResultSet rs) throws SQLException {
        CourseMaterial material = new CourseMaterial();
        material.setId(rs.getInt("id"));
        material.setCourseId(rs.getInt("course_id"));
        material.setTitle(rs.getString("title"));
        material.setDescription(rs.getString("description"));
        material.setFilePath(rs.getString("file_path"));
        material.setFileType(rs.getString("file_type"));
        material.setUploadedBy(rs.getInt("uploaded_by"));
        material.setUploadedByName(rs.getString("uploaded_by_name"));
        material.setUploadedAt(rs.getTimestamp("uploaded_at"));
        material.setStatus(rs.getString("status"));
        return material;
    }
}
