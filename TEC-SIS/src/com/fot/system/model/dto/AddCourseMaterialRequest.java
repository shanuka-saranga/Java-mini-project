package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class AddCourseMaterialRequest {
    private final String courseId;
    private final String courseCode;
    private final String title;
    private final String description;
    private final String filePath;
    private final String fileType;
    private final int uploadedBy;

    public AddCourseMaterialRequest(String courseId, String courseCode, String title, String description, String filePath, String fileType, int uploadedBy) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public int getUploadedBy() {
        return uploadedBy;
    }
}
