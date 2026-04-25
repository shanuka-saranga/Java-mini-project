package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

/**
 * carry edit course form input values
 * @author janith
 */
public class EditCourseRequest extends AddCourseRequest {
    private final int courseId;

    /**
     * initialize edit course request payload
     * @author janith
     */
    public EditCourseRequest(int courseId, String courseCode, String courseName, String credits, String totalHours,
                             String sessionType, String noOfQuizzes, String noOfAssignments, String departmentId, String lecturerInChargeId) {
        super(courseCode, courseName, credits, totalHours, sessionType, noOfQuizzes, noOfAssignments, departmentId, lecturerInChargeId);
        this.courseId = courseId;
    }

    public int getCourseId() {
        return courseId;
    }
}
