package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.AttendanceRepository;

import java.util.List;

/**
 * Handles technical officer medical approval business logic.
 * @author methum
 */
public class MedicalApprovalService implements IMedicalApprovalService {

    private final AttendanceRepository attendanceRepository;

    /**
     * Initializes the medical approval service.
     * @author methum
     */
    public MedicalApprovalService() {
        this.attendanceRepository = new AttendanceRepository();
    }

    /**
     * Loads pending medical submissions.
     * @author methum
     */
    public List<MedicalApprovalRow> getPendingMedicals() {
        return attendanceRepository.findMedicalRowsByStatus("PENDING");
    }

    /**
     * Loads approved medical submissions.
     * @author methum
     */
    public List<MedicalApprovalRow> getApprovedMedicals() {
        return attendanceRepository.findMedicalRowsByStatus("APPROVED");
    }

    /**
     * Validates and approves a medical submission.
     * @param medicalId medical id
     * @param approvedBy approver user id
     * @author methum
     */
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
