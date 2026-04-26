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

    /**
     * create add course material request payload
     * @param courseId course id
     * @param courseCode course code
     * @param title material title
     * @param description material description
     * @param filePath selected file path
     * @param fileType file extension/type
     * @param uploadedBy uploader user id
     * @author poornika
     */
    public AddCourseMaterialRequest(String courseId, String courseCode, String title, String description, String filePath, String fileType, int uploadedBy) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
    }

    /**
     * get request course id
     * @author poornika
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * get request course code
     * @author poornika
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * get material title
     * @author poornika
     */
    public String getTitle() {
        return title;
    }

    /**
     * get material description
     * @author poornika
     */
    public String getDescription() {
        return description;
    }

    /**
     * get selected file path
     * @author poornika
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * get selected file type
     * @author poornika
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * get uploader user id
     * @author poornika
     */
    public int getUploadedBy() {
        return uploadedBy;
    }
}
