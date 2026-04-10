package com.fot.system.model;

import java.util.ArrayList;
import java.util.List;

public class StudentMedicalRow {
    private int medicalId;
    private String submittedDate;
    private String approvalStatus;
    private String approvedAt;
    private String remarks;
    private String medicalDocument;
    private final List<MedicalSessionDetail> sessionDetails = new ArrayList<>();

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMedicalDocument() {
        return medicalDocument;
    }

    public void setMedicalDocument(String medicalDocument) {
        this.medicalDocument = medicalDocument;
    }

    public List<MedicalSessionDetail> getSessionDetails() {
        return sessionDetails;
    }

    public int getSessionCount() {
        return sessionDetails.size();
    }
}
