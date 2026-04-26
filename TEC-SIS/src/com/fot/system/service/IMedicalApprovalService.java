package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

/**
 * Defines the contract for TO medical approval operations.
 * @author methum
 */
public interface IMedicalApprovalService {
    /**
     * Loads pending medical submissions.
     * @author methum
     */
    List<MedicalApprovalRow> getPendingMedicals();

    /**
     * Loads approved medical submissions.
     * @author methum
     */
    List<MedicalApprovalRow> getApprovedMedicals();

    /**
     * Approves a medical submission.
     * @param medicalId medical id
     * @param approvedBy approver user id
     * @author methum
     */
    void approveMedical(int medicalId, int approvedBy);
}
