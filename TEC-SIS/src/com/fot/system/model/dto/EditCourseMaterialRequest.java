package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class EditCourseMaterialRequest {
    private final int materialId;
    private final String courseCode;
    private final String title;
    private final String description;
    private final String filePath;
    private final String fileType;
    private final int uploadedBy;

    /**
     * create edit course material request payload
     * @param materialId material id
     * @param courseCode course code
     * @param title material title
     * @param description material description
     * @param filePath selected file path
     * @param fileType file extension/type
     * @param uploadedBy updater user id
     * @author poornika
     */
    public EditCourseMaterialRequest(int materialId, String courseCode, String title, String description, String filePath, String fileType, int uploadedBy) {
        this.materialId = materialId;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
    }

    /**
     * get material id
     * @author poornika
     */
    public int getMaterialId() {
        return materialId;
    }

    /**
     * get course code
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
     * get updater user id
     * @author poornika
     */
    public int getUploadedBy() {
        return uploadedBy;
    }
}
