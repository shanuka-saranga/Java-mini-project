package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

import java.util.List;

public class AddStudentMedicalRequest {
    private final int studentUserId;
    private final String startDate;
    private final String endDate;
    private final List<Integer> sessionIds;
    private final String filePath;

    public AddStudentMedicalRequest(int studentUserId, String startDate, String endDate, List<Integer> sessionIds, String filePath) {
        this.studentUserId = studentUserId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionIds = sessionIds;
        this.filePath = filePath;
    }

    public int getStudentUserId() {
        return studentUserId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<Integer> getSessionIds() {
        return sessionIds;
    }

    public String getFilePath() {
        return filePath;
    }
}
