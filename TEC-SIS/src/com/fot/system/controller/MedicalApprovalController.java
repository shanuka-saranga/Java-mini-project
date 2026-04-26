package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.MedicalApprovalService;

import java.util.List;

/**
 * Coordinates medical approval actions between the TO medical view and service layer.
 * @author methum
 */
public class MedicalApprovalController {

    private final MedicalApprovalService medicalApprovalService;

    /**
     * Initializes the medical approval controller.
     * @author methum
     */
    public MedicalApprovalController() {
        this.medicalApprovalService = new MedicalApprovalService();
    }

    /**
     * Loads pending medical submissions.
     * @author methum
     */
    public List<MedicalApprovalRow> loadPendingMedicals() {
        return medicalApprovalService.getPendingMedicals();
    }

    /**
     * Loads approved medical submissions.
     * @author methum
     */
    public List<MedicalApprovalRow> loadApprovedMedicals() {
        return medicalApprovalService.getApprovedMedicals();
    }

    /**
     * Approves the selected medical record.
     * @param medicalId medical id
     * @param approvedBy approver user id
     * @author methum
     */
    public void approveMedical(int medicalId, int approvedBy) {
        medicalApprovalService.approveMedical(medicalId, approvedBy);
    }
}
