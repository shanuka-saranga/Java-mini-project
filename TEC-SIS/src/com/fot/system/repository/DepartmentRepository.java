package com.fot.system.repository;

import com.fot.system.config.DBConnection;
import com.fot.system.model.Department;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {

    private final Connection conn;

    public DepartmentRepository() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, dept_code, dept_name FROM departments ORDER BY dept_name";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Department department = new Department();
                department.setDepartmentId(rs.getInt("id"));
                department.setDeptCode(rs.getString("dept_code"));
                department.setDeptName(rs.getString("dept_name"));
                departments.add(department);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load departments: " + e.getMessage(), e);
        }

        return departments;
    }
}
