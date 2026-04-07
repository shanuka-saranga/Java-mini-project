package com.fot.system.model;

public class Department {
    private int departmentId;
    private String deptCode;
    private String deptName;

    public Department() {}

    public Department(int departmentId, String deptCode, String deptName) {
        this.departmentId = departmentId;
        this.deptCode = deptCode;
        this.deptName = deptName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return deptCode + " - " + deptName;
    }
}
