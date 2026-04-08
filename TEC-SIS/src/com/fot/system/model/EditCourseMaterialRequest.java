package com.fot.system.model;

public class EditCourseMaterialRequest {
    private final int materialId;
    private final String courseCode;
    private final String title;
    private final String description;
    private final String filePath;
    private final String fileType;
    private final int uploadedBy;

    public EditCourseMaterialRequest(int materialId, String courseCode, String title, String description, String filePath, String fileType, int uploadedBy) {
        this.materialId = materialId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
    }

    public int getMaterialId() {
        return materialId;
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
