package com.fot.system.model.entity;

import java.util.Date;

public class CourseMaterial {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private String filePath;
    private String fileType;
    private int uploadedBy;
    private String uploadedByName;
    private Date uploadedAt;
    private String status;

    /**
     * get material id
     * @author poornika
     */
    public int getId() {
        return id;
    }

    /**
     * set material id
     * @param id material id
     * @author poornika
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * get related course id
     * @author poornika
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * set related course id
     * @param courseId course id
     * @author poornika
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * get material title
     * @author poornika
     */
    public String getTitle() {
        return title;
    }

    /**
     * set material title
     * @param title title text
     * @author poornika
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get material description
     * @author poornika
     */
    public String getDescription() {
        return description;
    }

    /**
     * set material description
     * @param description description text
     * @author poornika
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get material file path
     * @author poornika
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * set material file path
     * @param filePath managed file path
     * @author poornika
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * get material file type
     * @author poornika
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * set material file type
     * @param fileType file extension/type
     * @author poornika
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * get uploader user id
     * @author poornika
     */
    public int getUploadedBy() {
        return uploadedBy;
    }

    /**
     * set uploader user id
     * @param uploadedBy uploader id
     * @author poornika
     */
    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    /**
     * get uploader display name
     * @author poornika
     */
    public String getUploadedByName() {
        return uploadedByName;
    }

    /**
     * set uploader display name
     * @param uploadedByName uploader full name
     * @author poornika
     */
    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    /**
     * get upload timestamp
     * @author poornika
     */
    public Date getUploadedAt() {
        return uploadedAt;
    }

    /**
     * set upload timestamp
     * @param uploadedAt upload datetime
     * @author poornika
     */
    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    /**
     * get material status
     * @author poornika
     */
    public String getStatus() {
        return status;
    }

    /**
     * set material status
     * @param status status value
     * @author poornika
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
