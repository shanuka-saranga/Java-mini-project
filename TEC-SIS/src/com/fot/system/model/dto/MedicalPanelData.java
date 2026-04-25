package com.fot.system.model.dto;

import java.util.List;

public class MedicalPanelData {
    private final List<MedicalApprovalRow> pendingRows;
    private final List<MedicalApprovalRow> approvedRows;

    public MedicalPanelData(List<MedicalApprovalRow> pendingRows, List<MedicalApprovalRow> approvedRows) {
        this.pendingRows = pendingRows;
        this.approvedRows = approvedRows;
    }

    public List<MedicalApprovalRow> getPendingRows() {
        return pendingRows;
    }

    public List<MedicalApprovalRow> getApprovedRows() {
        return approvedRows;
    }
}
