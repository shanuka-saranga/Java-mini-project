package com.fot.system.repository;

import com.fot.system.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NoticeRepository {
    private final Connection conn;

    public NoticeRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<String> findActiveNoticeMessagesForAudience(String audience) {
        List<String> notices = new ArrayList<>();
        String sql = """
                SELECT title, content
                FROM notices
                WHERE status = 'ACTIVE'
                  AND published_date <= CURDATE()
                  AND (expiry_date IS NULL OR expiry_date >= CURDATE())
                  AND (audience = 'ALL' OR audience = ?)
                ORDER BY priority DESC, published_date DESC, id DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, audience);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notices.add(rs.getString("title") + ": " + rs.getString("content"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load notices: " + e.getMessage(), e);
        }

        return notices;
    }
}
