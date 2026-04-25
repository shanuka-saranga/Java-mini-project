package com.fot.system.model.dto;

import com.fot.system.model.entity.Course;
import com.fot.system.model.entity.Notice;

import java.util.List;

public class LecturerDashboardData {
    private final List<Course> assignedCourses;
    private final int theoryCourses;
    private final int practicalOrBothCourses;
    private final int bothSessions;
    private final int totalCredits;
    private final int totalHours;
    private final int visibleNoticesCount;
    private final List<Notice> notices;

    public LecturerDashboardData(List<Course> assignedCourses, int theoryCourses, int practicalOrBothCourses,
                                 int bothSessions, int totalCredits, int totalHours,
                                 int visibleNoticesCount, List<Notice> notices) {
        this.assignedCourses = assignedCourses;
        this.theoryCourses = theoryCourses;
        this.practicalOrBothCourses = practicalOrBothCourses;
        this.bothSessions = bothSessions;
        this.totalCredits = totalCredits;
        this.totalHours = totalHours;
        this.visibleNoticesCount = visibleNoticesCount;
        this.notices = notices;
    }

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }

    public int getTheoryCourses() {
        return theoryCourses;
    }

    public int getPracticalOrBothCourses() {
        return practicalOrBothCourses;
    }

    public int getBothSessions() {
        return bothSessions;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public int getVisibleNoticesCount() {
        return visibleNoticesCount;
    }

    public List<Notice> getNotices() {
        return notices;
    }
}
