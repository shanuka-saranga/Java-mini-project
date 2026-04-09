package com.fot.system.model;

public class CourseAttendanceAggregate {
    private String registrationNo;
    private int courseId;
    private int totalHeldSessions;
    private int presentCount;
    private int approvedMedicalCount;

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTotalHeldSessions() {
        return totalHeldSessions;
    }

    public void setTotalHeldSessions(int totalHeldSessions) {
        this.totalHeldSessions = totalHeldSessions;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getApprovedMedicalCount() {
        return approvedMedicalCount;
    }

    public void setApprovedMedicalCount(int approvedMedicalCount) {
        this.approvedMedicalCount = approvedMedicalCount;
    }
}
