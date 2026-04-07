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
                return mapToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    public boolean existsByEmailExcludingUserId(String email, int userId) {
        return exists("SELECT 1 FROM users WHERE email = ? AND id <> ?", email, userId);
    }

    public boolean existsByPhone(String phone) {
        return exists("SELECT 1 FROM users WHERE phone = ?", phone);
    }

    public boolean existsByPhoneExcludingUserId(String phone, int userId) {
        return exists("SELECT 1 FROM users WHERE phone = ? AND id <> ?", phone, userId);
    }

    public boolean existsByRegistrationNo(String registrationNo) {
        return exists("SELECT 1 FROM student WHERE registration_no = ?", registrationNo);
    }

    public boolean existsByRegistrationNoExcludingUserId(String registrationNo, int userId) {
        return exists("SELECT 1 FROM student WHERE registration_no = ? AND user_id <> ?", registrationNo, userId);
    }

    public boolean existsByStaffCode(String staffCode) {
        return exists("SELECT 1 FROM staff WHERE staff_code = ?", staffCode);
    }

    public boolean existsByStaffCodeExcludingUserId(String staffCode, int userId) {
        return exists("SELECT 1 FROM staff WHERE staff_code = ? AND user_id <> ?", staffCode, userId);
    }

    public boolean save(User user) {
        String sqlUser = "INSERT INTO users (first_name, last_name, role, dob, email, phone, address, department_id, password_hash, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                stmt.setString(10, user.getStatus());

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

    public boolean update(User user) {
        String sqlUser = "UPDATE users SET first_name = ?, last_name = ?, role = ?, dob = ?, email = ?, phone = ?, address = ?, department_id = ?, password_hash = ?, status = ? WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlUser)) {
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getRole());
                stmt.setDate(4, new java.sql.Date(user.getDob().getTime()));
                stmt.setString(5, user.getEmail());
                stmt.setString(6, user.getPhone());
                stmt.setString(7, user.getAddress());
                stmt.setInt(8, user.getDepartmentId());
                stmt.setString(9, user.getPasswordHash());
                stmt.setString(10, user.getStatus());
                stmt.setInt(11, user.getId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating user failed.");
                }

                updateRoleSpecificDetails(user);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
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

    private void updateRoleSpecificDetails(User user) throws SQLException {
        if (user instanceof Student) {
            updateStudentDetails((Student) user);
        } else if (user instanceof Staff) {
            updateStaffDetails((Staff) user);
        }
    }

    private void updateStudentDetails(Student student) throws SQLException {
        String sql = "UPDATE student SET registration_no = ?, registration_year = ?, student_type = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getRegistrationNo());
            stmt.setInt(2, student.getRegistrationYear());
            stmt.setString(3, student.getStudentType());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        }
    }

    private void updateStaffDetails(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET staff_code = ?, designation = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getStaffCode());
            stmt.setString(2, staff.getDesignation());
            stmt.setInt(3, staff.getId());
            stmt.executeUpdate();
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT u.*, s.registration_no, s.registration_year, s.student_type, " +
                "st.staff_code, st.designation " +
                "FROM users u " +
                "LEFT JOIN student s ON u.id = s.user_id " +
                "LEFT JOIN staff st ON u.id = st.user_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // දැන් මේක ඇතුළේ staff_code තියෙන නිසා Error එකක් එන්නේ නැහැ
                users.add(mapToSpecificUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error in findAll: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    private User mapToSpecificUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;

        if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(role)) {
            user = new Student();
            ((Student) user).setRegistrationNo(rs.getString("registration_no"));
            ((Student) user).setRegistrationYear(rs.getInt("registration_year"));
            ((Student) user).setStudentType(rs.getString("student_type"));
        } else {
            user = new Staff();
            ((Staff) user).setStaffCode(rs.getString("staff_code"));
            ((Staff) user).setDesignation(rs.getString("designation"));
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

    private User mapToUser(ResultSet rs) throws SQLException {
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

    public User findById(int id) {
        String sql = "SELECT u.*, s.registration_no, s.registration_year, s.student_type, " +
                "st.staff_code, st.designation " +
                "FROM users u " +
                "LEFT JOIN student s ON u.id = s.user_id " +
                "LEFT JOIN staff st ON u.id = st.user_id " +
                "WHERE u.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToSpecificUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    private boolean exists(String sql, String value) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database check failed: " + e.getMessage(), e);
        }
    }

    private boolean exists(String sql, String value, int userId) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database check failed: " + e.getMessage(), e);
        }
    }
}
