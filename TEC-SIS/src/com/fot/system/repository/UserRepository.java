package com.fot.system.repository;

import com.fot.system.config.AppConfig;
import com.fot.system.config.DBConnection;
import com.fot.system.model.entity.Staff;
import com.fot.system.model.entity.Student;
import com.fot.system.model.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * handle user persistence and lookup queries
 * @author janith
 */
public class UserRepository {
    private static final String USER_WITH_ROLE_DETAILS_SQL =
            "SELECT u.*, s.registration_no, s.registration_year, s.student_type, " +
            "st.staff_code, st.designation " +
            "FROM users u " +
            "LEFT JOIN student s ON u.id = s.user_id " +
            "LEFT JOIN staff st ON u.id = st.user_id";

    private final Connection conn;

    /**
     * initialize user repository and get database connection
     * @author janith
     */
    public UserRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * find user by id
     * @param email user email
     * @author methum
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user by email: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * check if user exists by email
     * @param email user email
     * @author janith
     */
    public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    /**
     * check if user exists by email and user id
     * @param email user email
     * @author janith
     */
    public boolean existsByEmailAndUserId(String email, int userId) {
        return exists("SELECT 1 FROM users WHERE email = ? AND id <> ?", email, userId);
    }

    /**
     * check phone number is already exist
     * @param phone user phone
     * @author janith
     */
    public boolean existsByPhone(String phone) {
        return exists("SELECT 1 FROM users WHERE phone = ?", phone);
    }

    /**
     * check user phone number is already exist
     * @param phone user phone
     * @param userId user id to exclude from check
     * @author janith
     */
    public boolean existsByPhoneExcludingUserId(String phone, int userId) {
        return exists("SELECT 1 FROM users WHERE phone = ? AND id <> ?", phone, userId);
    }

    /**
     * check registration number is already exist
     * @param registrationNo student String type registration number
     * @author janith
     */
    public boolean existsByRegistrationNo(String registrationNo) {
        return exists("SELECT 1 FROM student WHERE registration_no = ?", registrationNo);
    }

    /**
     * check user registration number is already exist
     * @param registrationNo student String type registration number
     * @param userId int Specific user id
     * @author janith
     */
    public boolean existsByRegistrationNoExcludingUserId(String registrationNo, int userId) {
        return exists("SELECT 1 FROM student WHERE registration_no = ? AND user_id <> ?", registrationNo, userId);
    }

    /**
     * check staff Code is already exist
     * @param staffCode String specific staff code
     * @author janith
     */
    public boolean existsByStaffCode(String staffCode) {
        return exists("SELECT 1 FROM staff WHERE staff_code = ?", staffCode);
    }

    /**
     * check user staff Code is already exist
     * @param staffCode String specific staff code
     * @param userId int Specific user id
     * @author janith
     */
    public boolean existsByStaffCodeExcludingUserId(String staffCode, int userId) {
        return exists("SELECT 1 FROM staff WHERE staff_code = ? AND user_id <> ?", staffCode, userId);
    }

    /**
     * save user to database
     * @param user User object
     * @author janith
     */
    public boolean save(User user) {
        String sqlUser = "INSERT INTO users (first_name, last_name, role, dob, email, phone, address, profile_picture_path, department_id, password_hash, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                stmt.setString(8, user.getProfilePicturePath());
                stmt.setInt(9, user.getDepartmentId());
                stmt.setString(10, user.getPasswordHash());
                stmt.setString(11, user.getStatus());

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
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    /**
     * update user details in database
     * @param user User object
     * @author janith
     */
    public boolean update(User user) {
        String sqlUser = "UPDATE users SET first_name = ?, last_name = ?, role = ?, dob = ?, email = ?, phone = ?, address = ?, profile_picture_path = ?, department_id = ?, password_hash = ?, status = ? WHERE id = ?";

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
                stmt.setString(8, user.getProfilePicturePath());
                stmt.setInt(9, user.getDepartmentId());
                stmt.setString(10, user.getPasswordHash());
                stmt.setString(11, user.getStatus());
                stmt.setInt(12, user.getId());

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

    /**
     * delete user from database by id
     * @param userId int user id to delete
     * @author janith
     */
    public boolean deleteById(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * save student details to database
     * @param s Student object (student table data)
     * @author janith
     */
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

    /**
     * save staff details to database
     * @param s Staff object (staff table data)
     * @author janith
     */
    private void saveStaffDetails(Staff s) throws SQLException {
        String sql = "INSERT INTO staff (user_id, staff_code, designation) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getId());
            stmt.setString(2, s.getStaffCode());
            stmt.setString(3, s.getDesignation());
            stmt.executeUpdate();
        }
    }

    /**
     * update role specific details in database
     * @param user User object
     * @author janith
     */
    private void updateRoleSpecificDetails(User user) throws SQLException {
        if (user instanceof Student) {
            updateStudentDetails((Student) user);
        } else if (user instanceof Staff) {
            updateStaffDetails((Staff) user);
        }
    }

    /**
     * update student details in database
     * @param student Student object (student table data)
     * @author janith
     */
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

    /**
     * update user details in database
     * @param staff Staff object (staff table data)
     * @author janith
     */
    private void updateStaffDetails(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET staff_code = ?, designation = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getStaffCode());
            stmt.setString(2, staff.getDesignation());
            stmt.setInt(3, staff.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * get all users from database
     * @author janith
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(USER_WITH_ROLE_DETAILS_SQL)) {

            while (rs.next()) {
                users.add(mapToSpecificUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load users: " + e.getMessage(), e);
        }

        return users;
    }

    /**
     * count all users in database
     * @author janith
     */
    public int countAll() {
        return countBySql("SELECT COUNT(*) FROM users");
    }

    /**
     * count users by role
     * @param role user role
     * @author janith
     */
    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * map query result to specific user type (student or staff)
     * @param rs ResultSet object
     * @author janith
     */
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

        mapCommonUserColumns(rs, user, role);
        return user;
    }

    /**
     * map user table result to basic user object
     * @param rs ResultSet object
     * @author janith
     */
    private User mapToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;

        if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(role)) {
            user = new Student();
        } else {
            user = new Staff();
        }

        mapCommonUserColumns(rs, user, role);
        return user;
    }

    /**
     * find user by id
     * @param id user id
     * @author janith
     */
    public User findById(int id) {
        String sql = USER_WITH_ROLE_DETAILS_SQL + " WHERE u.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToSpecificUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user by ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * execute generic existence check by single value
     * @param sql sql query with one parameter
     * @param value value to bind
     * @author janith
     */
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

    /**
     * execute generic existence check by value and excluded user id
     * @param sql sql query with two parameters
     * @param value value to bind
     * @param userId user id to exclude
     * @author janith
     */
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

    /**
     * execute generic count query
     * @param sql count sql query
     * @author janith
     */
    private int countBySql(String sql) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * map common user columns from result set
     * @param rs source result set
     * @param user target user entity
     * @param role normalized role value
     * @author janith
     */
    private void mapCommonUserColumns(ResultSet rs, User user, String role) throws SQLException {
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setProfilePicturePath(rs.getString("profile_picture_path"));
        user.setDob(rs.getDate("dob"));
        user.setDepartmentId(rs.getInt("department_id"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setStatus(rs.getString("status"));
        user.setRole(role);
    }
}
