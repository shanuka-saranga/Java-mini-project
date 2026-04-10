package com.fot.system.service;

import com.fot.system.model.MedicalApprovalRow;
import com.fot.system.repository.AttendanceRepository;

import java.util.List;

public class MedicalApprovalService {

    private final AttendanceRepository attendanceRepository;

    public MedicalApprovalService() {
        this.attendanceRepository = new AttendanceRepository();
    }

    public List<MedicalApprovalRow> getPendingMedicals() {
        return attendanceRepository.findMedicalRowsByStatus("PENDING");
    }

    public List<MedicalApprovalRow> getApprovedMedicals() {
        return attendanceRepository.findMedicalRowsByStatus("APPROVED");
    }

    public void approveMedical(int medicalId, int approvedBy) {
        if (medicalId <= 0) {
            throw new RuntimeException("Invalid medical record.");
        }
        if (approvedBy <= 0) {
            throw new RuntimeException("Invalid approver.");
        }
        attendanceRepository.approveMedical(medicalId, approvedBy);
    }
}
