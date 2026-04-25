package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

import java.util.ArrayList;
import java.util.List;

public class StudentsPerformance extends Student {

    private List<CourseMarksAndAttendanceDetail> courseMarks = new ArrayList<>();

    public List<CourseMarksAndAttendanceDetail> getCourseMarks() { return courseMarks; }

    public static class CourseMarksAndAttendanceDetail {
        private String courseCode;
        private String sessionType;
        private String courseName;
        private double quizAvg;
        private double assignmentTotal;
        private double midExamMark;
        private double endExamMark;

        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public double getQuizAvg() { return quizAvg; }
        public void setQuizAvg(double quizAvg) { this.quizAvg = quizAvg; }
        public double getAssignmentTotal() { return assignmentTotal; }
        public void setAssignmentTotal(double assignmentTotal) { this.assignmentTotal = assignmentTotal; }
        public double getMidExamMark() { return midExamMark; }
        public void setMidExamMark(double midExamMark) { this.midExamMark = midExamMark; }
        public double getEndExamMark() { return endExamMark; }
        public void setEndExamMark(double endExamMark) { this.endExamMark = endExamMark; }
        public void setSessionType(String sessionType) { this.sessionType = sessionType; }
        public String getSessionType() { return sessionType; }

        public double getCaTotal() { return (quizAvg + assignmentTotal + midExamMark) / 3.0;}

        @Override
        public String toString() {
            String truncatedName = (courseName != null && courseName.length() > 25)
                    ? courseName.substring(0, 22) + "..."
                    : (courseName != null ? courseName : "N/A");

            return String.format("| %-10s | %-25s | %6.2f | %6.2f | %6.2f | %6.2f |",
                    courseCode != null ? courseCode : "N/A", truncatedName,
                    quizAvg, assignmentTotal, midExamMark, endExamMark);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+------------+---------------------------+--------+--------+--------+--------+\n");
        sb.append(String.format("| Student: %-25s Reg No: %-15s |\n",
                getFirstName() + " " + getLastName(),
                getRegistrationNo()));
        sb.append("+------------+---------------------------+--------+--------+--------+--------+\n");
        sb.append("| Code       | Course Name               | Quiz   | Assign | Mid    | End    |\n");
        sb.append("+------------+---------------------------+--------+--------+--------+--------+\n");

        if (courseMarks == null || courseMarks.isEmpty()) {
            sb.append("|                     No course marks found.                               |\n");
        } else {
            for (CourseMarksAndAttendanceDetail detail : courseMarks) {
                sb.append(detail.toString()).append("\n");
            }
        }
        sb.append("+------------+---------------------------+--------+--------+--------+--------+\n");
        return sb.toString();
    }
}