package com.fot.system.model.dto;


import java.util.List;

/**
 * aggregate lecturer attendance page data sections for one course
 * @author poornika
 */
public class CourseAttendanceViewData {
    private AttendanceCourseProgress courseProgress;
    private List<AttendanceTableRow> attendanceRows;
    private List<StudentAttendanceSummaryRow> studentSummaryRows;

    public AttendanceCourseProgress getCourseProgress() {
        return courseProgress;
    }

    public void setCourseProgress(AttendanceCourseProgress courseProgress) {
        this.courseProgress = courseProgress;
    }

    public List<AttendanceTableRow> getAttendanceRows() {
        return attendanceRows;
    }

    public void setAttendanceRows(List<AttendanceTableRow> attendanceRows) {
        this.attendanceRows = attendanceRows;
    }

    public List<StudentAttendanceSummaryRow> getStudentSummaryRows() {
        return studentSummaryRows;
    }

    public void setStudentSummaryRows(List<StudentAttendanceSummaryRow> studentSummaryRows) {
        this.studentSummaryRows = studentSummaryRows;
    }
}
