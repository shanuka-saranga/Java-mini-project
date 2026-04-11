package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class EditCourseRequest extends AddCourseRequest {
    private final int courseId;

    public EditCourseRequest(int courseId, String courseCode, String courseName, String credits, String totalHours,
                             String sessionType, String noOfQuizzes, String noOfAssignments, String departmentId, String lecturerInChargeId) {
        super(courseCode, courseName, credits, totalHours, sessionType, noOfQuizzes, noOfAssignments, departmentId, lecturerInChargeId);
        this.courseId = courseId;
    }

    public int getCourseId() {
        return courseId;
    }
}
