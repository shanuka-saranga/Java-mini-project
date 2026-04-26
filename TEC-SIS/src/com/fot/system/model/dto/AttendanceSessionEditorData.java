package com.fot.system.model.dto;

import java.util.List;

/**
 * Holds session details and editable student attendance rows.
 * @author methum
 */
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
