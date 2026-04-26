package com.fot.system.model.dto;

import java.util.List;

/**
 * Carries pending and approved medical lists for the TO medical panel.
 * @author methum
 */
public class MedicalPanelData {
    private final List<MedicalApprovalRow> pendingRows;
    private final List<MedicalApprovalRow> approvedRows;

    /**
     * Creates the TO medical panel data bundle.
     * @param pendingRows pending medical rows
     * @param approvedRows approved medical rows
     * @author methum
     */
    public MedicalPanelData(List<MedicalApprovalRow> pendingRows, List<MedicalApprovalRow> approvedRows) {
        this.pendingRows = pendingRows;
        this.approvedRows = approvedRows;
    }

    /**
     * Returns pending medical rows.
     * @author methum
     */
    public List<MedicalApprovalRow> getPendingRows() {
        return pendingRows;
    }

    /**
     * Returns approved medical rows.
     * @author methum
     */
    public List<MedicalApprovalRow> getApprovedRows() {
        return approvedRows;
    }
}
