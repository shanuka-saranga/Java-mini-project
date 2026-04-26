package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface IMedicalApprovalService {
    List<MedicalApprovalRow> getPendingMedicals();
    List<MedicalApprovalRow> getApprovedMedicals();
    void approveMedical(int medicalId, int approvedBy);
}
