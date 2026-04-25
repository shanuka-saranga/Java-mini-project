package com.fot.system.model.dto;

import com.fot.system.model.entity.Notice;

import java.util.List;

public class AdminDashboardData {
    private final int totalUsers;
    private final int lecturers;
    private final int courses;
    private final int visibleNoticesCount;
    private final int students;
    private final int technicalOfficers;
    private final int admins;
    private final List<Notice> notices;

    /**
     * create admin dashboard dto object
     * @param totalUsers total user count
     * @param lecturers lecturer count
     * @param courses course count
     * @param visibleNoticesCount visible notice count
     * @param students student count
     * @param technicalOfficers technical officer count
     * @param admins admin count
     * @param notices notice list
     * @author janith
     */
    public AdminDashboardData(int totalUsers, int lecturers, int courses, int visibleNoticesCount,
                              int students, int technicalOfficers, int admins, List<Notice> notices) {
        this.totalUsers = totalUsers;
        this.lecturers = lecturers;
        this.courses = courses;
        this.visibleNoticesCount = visibleNoticesCount;
        this.students = students;
        this.technicalOfficers = technicalOfficers;
        this.admins = admins;
        this.notices = notices;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getLecturers() {
        return lecturers;
    }

    public int getCourses() {
        return courses;
    }

    public int getVisibleNoticesCount() {
        return visibleNoticesCount;
    }

    public int getStudents() {
        return students;
    }

    public int getTechnicalOfficers() {
        return technicalOfficers;
    }

    public int getAdmins() {
        return admins;
    }

    public List<Notice> getNotices() {
        return notices;
    }
}
