package com.fot.system.controller;

import com.fot.system.model.MedicalApprovalRow;
import com.fot.system.service.MedicalApprovalService;

import java.util.List;

public class MedicalApprovalController {

    private final MedicalApprovalService medicalApprovalService;

    public MedicalApprovalController() {
        this.medicalApprovalService = new MedicalApprovalService();
    }

    public List<MedicalApprovalRow> loadPendingMedicals() {
        return medicalApprovalService.getPendingMedicals();
    }

    public List<MedicalApprovalRow> loadApprovedMedicals() {
        return medicalApprovalService.getApprovedMedicals();
    }

    public void approveMedical(int medicalId, int approvedBy) {
        medicalApprovalService.approveMedical(medicalId, approvedBy);
    }
}
