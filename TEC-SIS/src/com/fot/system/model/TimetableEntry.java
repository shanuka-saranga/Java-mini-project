package com.fot.system.model;

public class TimetableEntry {

    private String day;
    private String timeRange;
    private String courseName;
    private String venue;
    private String lecturer;

    public TimetableEntry() {
    }

    public TimetableEntry(String day, String timeRange, String courseName, String venue, String lecturer) {
        this.day = day;
        this.timeRange = timeRange;
        this.courseName = courseName;
        this.venue = venue;
        this.lecturer = lecturer;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }
}