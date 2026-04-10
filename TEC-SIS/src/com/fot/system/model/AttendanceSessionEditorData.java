package com.fot.system.model;

import java.util.List;

public class AttendanceSessionEditorData {
    private AttendanceSessionRow session;
    private List<StudentAttendanceEditRow> studentRows;

    public AttendanceSessionRow getSession() {
        return session;
    }

    public void setSession(AttendanceSessionRow session) {
        this.session = session;
    }

    public List<StudentAttendanceEditRow> getStudentRows() {
        return studentRows;
    }

    public void setStudentRows(List<StudentAttendanceEditRow> studentRows) {
        this.studentRows = studentRows;
    }
}
