package com.fot.system.repository;

import com.fot.system.config.AppConfig;
import com.fot.system.config.DBConnection;
import com.fot.system.model.Staff;
import com.fot.system.model.Student;
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
                String currentStatus = rs.getString("status");
                System.out.println("User Status from DB: " + currentStatus);
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(User user) {
        String sqlUser = "INSERT INTO users (first_name, last_name, role, dob, email, phone, address, department_id, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getRole());
                stmt.setDate(4, new java.sql.Date(user.getDob().getTime()));
                stmt.setString(5, user.getEmail());
                stmt.setString(6, user.getPhone());
                stmt.setString(7, user.getAddress());
                stmt.setInt(8, user.getDepartmentId());
                stmt.setString(9, user.getPasswordHash());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) throw new SQLException("Creating user failed.");

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newUserId = generatedKeys.getInt(1);
                        user.setId(newUserId);

                        if (user instanceof Student) {
                            saveStudentDetails((Student) user);
                        } else if (user instanceof Staff) {
                            saveStaffDetails((Staff) user);
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveStudentDetails(Student s) throws SQLException {
        String sql = "INSERT INTO student (user_id, registration_no, registration_year, student_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            stmt.setString(2, s.getRegistrationNo());
            stmt.setInt(3, s.getRegistrationYear());
            stmt.setString(4, s.getStudentType());
            stmt.executeUpdate();
        }
    }

    private void saveStaffDetails(Staff s) throws SQLException {
        String sql = "INSERT INTO staff (user_id, staff_code, designation) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            stmt.setString(2, s.getStaffCode());
            stmt.setString(3, s.getDesignation());
            stmt.executeUpdate();
        }
    }

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

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;

        if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(role)) {
            user = new Student();
        } else {
            user = new Staff();
        }

        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setDob(rs.getDate("dob"));
        user.setDepartmentId(rs.getInt("department_id"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setStatus(rs.getString("status"));
        user.setRole(role);

        return user;
    }
}