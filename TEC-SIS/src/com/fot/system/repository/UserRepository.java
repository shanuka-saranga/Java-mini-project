package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final Connection conn;

    public UserRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 💾 Save user (INSERT)
    public boolean save(User user) {

        String sql = "INSERT INTO users (first_name, last_name, role, dob, email, phone, address, department_id, password_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getRole());

            // Convert java.util.Date → java.sql.Date
            stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));

            stmt.setString(5, user.getEmail());
            stmt.setString(6, "0000000000"); // placeholder
            stmt.setString(7, "N/A");        // placeholder
            stmt.setInt(8, 1);               // default department
            stmt.setString(9, user.getPasswordHash());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 📋 Get all users (ADMIN FEATURE)
    public List<User> findAll() {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // 🔄 Map ResultSet → User Object (Reusable)
    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User user = new User() {
            @Override
            public void displayDashboard() {
                System.out.println("Dashboard for: " + getRole());
            }
        };

        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));

        return user;
    }
}